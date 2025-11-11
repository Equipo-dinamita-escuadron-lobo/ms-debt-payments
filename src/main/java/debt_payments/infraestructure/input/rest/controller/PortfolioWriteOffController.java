package debt_payments.infraestructure.input.rest.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import debt_payments.application.input.IPortfolioWriteOffCommandUseCase;
import debt_payments.application.input.IPortfolioWriteOffQueryUseCase;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.WriteOffDetail;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.input.rest.dto.request.CreateWriteOffRequest;
import debt_payments.infraestructure.input.rest.dto.request.WriteOffDetailRequest;
import debt_payments.infraestructure.input.rest.dto.response.InvoiceSummaryResponse;
import debt_payments.infraestructure.input.rest.dto.response.PortfolioWriteOffResponse;
import debt_payments.infraestructure.input.rest.dto.response.WriteOffDetailResponse;
import debt_payments.infraestructure.input.rest.mapper.IPortfolioWriteOffRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/write-offs")
@RequiredArgsConstructor
public class PortfolioWriteOffController {
    
    private final IPortfolioWriteOffCommandUseCase commandUseCase;
    private final IPortfolioWriteOffQueryUseCase queryUseCase;
    private final IPortfolioWriteOffRestMapper portfolioWriteOffRestMapper;
    private final IInvoiceProviderPort invoiceProviderPort;

    @PostMapping("/")
    public ResponseEntity<PortfolioWriteOffResponse> createWriteOff(@Valid @RequestBody CreateWriteOffRequest request) {
        
        // 1. Enriquecer los detalles: Buscar facturas para obtener los datos faltantes
        List<Long> invoiceIds = request.getDetails().stream().map(WriteOffDetailRequest::getInvoiceId).toList();
        Map<Long, InvoiceReplica> invoiceMap = invoiceProviderPort.findInvoicesByIds(invoiceIds).stream()
                .collect(Collectors.toMap(InvoiceReplica::getId, Function.identity()));

        if (invoiceMap.size() != invoiceIds.size()) {
            throw new InvoiceNotFoundException("One or more invoices specified in the request do not exist.");
        }

        // 2. Construir la lista de detalles de dominio COMPLETOS
        List<WriteOffDetail> domainDetails = request.getDetails().stream()
                .map(detailRequest -> {
                    InvoiceReplica invoice = invoiceMap.get(detailRequest.getInvoiceId());
                    // Creamos el objeto de dominio WriteOffDetail con toda la info
                    return WriteOffDetail.builder()
                            .invoiceId(invoice.getId())
                            .amountWrittenOff(invoice.getPendingValue())
                            .accountingAccount(invoice.getAccountingAccount())
                            .build();
                })
                .collect(Collectors.toList());

        // 3. Crear el Agregado de Dominio usando su fábrica
        PortfolioWriteOff domainToCreate = PortfolioWriteOff.create(
            request.getEnterpriseId(),
            request.getThirdId(),
            request.getJustification(),
            domainDetails
        );
        
        // 4. Llamar al servicio de aplicación con un objeto de dominio válido
        domainToCreate.setWriteOffDate(request.getWriteOffDate());
        domainToCreate.setDebitAuxiliaryAccount(request.getDebitAuxiliaryAccount());
        domainToCreate.setDebitAuxiliaryAccountId(request.getDebitAuxiliaryAccountId());
        PortfolioWriteOff createdDomain = commandUseCase.createWriteOff(domainToCreate);
        
        // 5. Construir la respuesta enriquecida (esta lógica ya la tenías)
        PortfolioWriteOffResponse response = buildEnrichedResponse(createdDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<PortfolioWriteOffResponse> confirmWriteOff(@PathVariable Long id) {
        PortfolioWriteOff confirmedDomain = commandUseCase.confirmWriteOff(id);
        PortfolioWriteOffResponse response = buildEnrichedResponse(confirmedDomain);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/void")
    public ResponseEntity<PortfolioWriteOffResponse> voidWriteOff(@PathVariable Long id) {
        PortfolioWriteOff voidedDomain = commandUseCase.voidWriteOffConfirmation(id);
        PortfolioWriteOffResponse response = buildEnrichedResponse(voidedDomain);
        return ResponseEntity.ok(response);
    }

    // --- Endpoints de CONSULTAS (GET) ---

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioWriteOffResponse> getWriteOffById(@PathVariable Long id) {
        return queryUseCase.findById(id)
                .map(this::buildEnrichedResponse) // Reutilizamos el método de enriquecimiento
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-enterprise/{enterpriseId}")
    public ResponseEntity<List<PortfolioWriteOffResponse>> getByEnterprise(@PathVariable String enterpriseId) {
        List<PortfolioWriteOff> domainList = queryUseCase.findByEnterpriseId(enterpriseId);
        if (domainList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<PortfolioWriteOffResponse> responseList = domainList.stream()
                .map(this::buildEnrichedResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
 * Construye un DTO de respuesta enriquecido para un castigo, incluyendo detalles de las facturas.
 * Este método es responsabilidad de la capa de infraestructura (Controller) porque se ocupa
 * de la "forma" de la respuesta HTTP.
 *
 * @param writeOff El objeto de dominio PortfolioWriteOff a partir del cual se construye la respuesta.
 * @return El DTO PortfolioWriteOffResponse completamente construido y listo para ser enviado.
 */
private PortfolioWriteOffResponse buildEnrichedResponse(PortfolioWriteOff writeOff) {
    // 1. Obtener todos los IDs de las facturas de los detalles del castigo
    List<Long> invoiceIds = writeOff.getDetails().stream()
                                    .map(WriteOffDetail::getInvoiceId)
                                    .toList();
    
    // Si no hay detalles, no hay nada que enriquecer, devolvemos la respuesta base.
    if (invoiceIds.isEmpty()) {
        return portfolioWriteOffRestMapper.toResponse(writeOff);
    }

    // 2. Obtener la información de todas esas facturas en una sola llamada para eficiencia
    Map<Long, InvoiceReplica> invoiceMap = invoiceProviderPort.findInvoicesByIds(invoiceIds).stream()
        .collect(Collectors.toMap(InvoiceReplica::getId, Function.identity()));

    // 3. Construir la lista de detalles de respuesta (WriteOffDetailResponse)
    List<WriteOffDetailResponse> detailResponses = writeOff.getDetails().stream()
        .map(detail -> {
            InvoiceReplica invoice = invoiceMap.get(detail.getInvoiceId());
            
            // Caso de guarda: si por alguna inconsistencia de datos la factura no se encuentra,
            // se omite este detalle para no romper la respuesta.
            if (invoice == null) {
                // Opcionalmente, puedes loggear una advertencia aquí.
                // log.warn("Invoice with id {} not found for WriteOff id {}", detail.getInvoiceId(), writeOff.getId());
                return null;
            }
            
            // 3a. Construir el resumen de la factura (InvoiceSummaryResponse)
            InvoiceSummaryResponse invoiceSummary = InvoiceSummaryResponse.builder()
                .id(invoice.getId())
                .factCode(invoice.getFactCode())
                .totalValue(invoice.getTotalValue())
                .pendingValue(detail.getAmountWrittenOff())
                .expirationDate(invoice.getExpirationDate())
                .accountingAccount(invoice.getAccountingAccount())
                .build();
            
            // 3b. Construir el detalle del castigo (WriteOffDetailResponse)
            return WriteOffDetailResponse.builder()
                .amountWrittenOff(detail.getAmountWrittenOff())
                .invoice(invoiceSummary) // Anidar el resumen de la factura
                .build();
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    // 4. Construir y devolver el DTO de respuesta final (PortfolioWriteOffResponse)
    // Usamos el patrón builder para ensamblar la respuesta final.
    return PortfolioWriteOffResponse.builder()
        .id(writeOff.getId())
        .code(writeOff.getCode())
        .justification(writeOff.getJustification())
        .totalAmount(writeOff.getTotalAmount())
        .writeOffDate(writeOff.getWriteOffDate())
        .debitAuxiliaryAccount(writeOff.getDebitAuxiliaryAccount())
        .debitAuxiliaryAccountId(writeOff.getDebitAuxiliaryAccountId())
        .thirdId(writeOff.getThirdId())
        .status(writeOff.getStatus())
        .enterpriseId(writeOff.getEnterpriseId())
        .details(detailResponses) // Asignar la lista de detalles enriquecidos
        .build();
}
}
