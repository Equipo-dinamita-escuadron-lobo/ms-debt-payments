package debt_payments.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Map;
import org.springframework.stereotype.Service;

import debt_payments.application.input.IAccountingEventPublisher;
import debt_payments.application.input.IPortfolioWriteOffCommandUseCase;
import debt_payments.application.input.IPortfolioWriteOffQueryUseCase;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IPortfolioWriteOffPersistencePort;
import debt_payments.domain.enums.WriteOffStatus;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.WriteOffDetail;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.input.rest.dto.request.CreateWriteOffRequest;
import debt_payments.infraestructure.input.rest.dto.request.WriteOffDetailRequest;
import debt_payments.infraestructure.input.rest.dto.response.InvoiceSummaryResponse;
import debt_payments.infraestructure.input.rest.dto.response.PortfolioWriteOffResponse;
import debt_payments.infraestructure.input.rest.dto.response.WriteOffDetailResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioWriteOffService implements IPortfolioWriteOffCommandUseCase, IPortfolioWriteOffQueryUseCase {

    private final IPortfolioWriteOffPersistencePort writeOffPersistencePort;
    private final IInvoiceProviderPort invoiceProviderPort;
    private final IAccountingEventPublisher accountingEventPublisher;

    @Override
    public PortfolioWriteOff createWriteOff(CreateWriteOffRequest request) {

        Long totalAmount = 0L;
        // 1. Validar que las facturas existen y obtener sus saldos
        List<Long> invoiceIds = request.getDetails().stream()
                .map(WriteOffDetailRequest::getInvoiceId)
                .toList();

        List<InvoiceReplica> foundInvoices = invoiceProviderPort.findInvoicesByIds(invoiceIds);
        if (foundInvoices.size() != invoiceIds.size()) {
            throw new InvoiceNotFoundException("One or more invoices specified in the write-off do not exist.");
        }

        // Convertir la lista a un mapa para búsqueda fácil
        Map<Long, InvoiceReplica> invoiceMap = foundInvoices.stream()
                .collect(Collectors.toMap(InvoiceReplica::getId, Function.identity()));

        // Calcular el monto total a castigar
        for (WriteOffDetailRequest detailRequest : request.getDetails()) {
            InvoiceReplica invoice = invoiceMap.get(detailRequest.getInvoiceId());
            totalAmount += invoice.getPendingValue(); // Sumamos el saldo pendiente ACTUAL
        }

        // 2. Construir el objeto de dominio a partir del DTO
        List<WriteOffDetail> details = new ArrayList<>();
        for (WriteOffDetailRequest detailRequest : request.getDetails()) {
            InvoiceReplica invoice = invoiceMap.get(detailRequest.getInvoiceId());
            details.add(WriteOffDetail.builder()
                    .invoiceId(invoice.getId())
                    .amountWrittenOff(invoice.getPendingValue()) // Guardamos el saldo pendiente ACTUAL
                    .build());
        }

        PortfolioWriteOff newWriteOff = PortfolioWriteOff.builder()
                .code(generateUniqueWriteOffCode())
                .justification(request.getJustification())
                .totalAmount(totalAmount)
                .writeOffDate(request.getWriteOffDate())
                .debitAuxiliaryAccount(request.getDebitAuxiliaryAccount())
                .debitAuxiliaryAccountId(request.getDebitAuxiliaryAccountId())
                .thirdId(request.getThirdId())
                .enterpriseId(request.getEnterpriseId()) 
                .status(WriteOffStatus.PENDING_CONFIRMATION)
                .details(details)
                .build();

        // 3. Persistir usando el puerto de salida
        return writeOffPersistencePort.save(newWriteOff);
    }

    @Override
    public PortfolioWriteOff confirmWriteOff(Long writeOffId) {
        // 1. Obtener el agregado raíz
        PortfolioWriteOff writeOff = findWriteOffOrThrow(writeOffId);

        // 2. Ejecutar la lógica de negocio del dominio
        writeOff.confirm();

        // 3. Aplicar los efectos secundarios (modificar facturas)
        for (WriteOffDetail detail : writeOff.getDetails()) {
            InvoiceReplica invoice = findInvoiceOrThrow(detail.getInvoiceId());
            invoice.setPendingValue(0L); // Castigamos el saldo a cero
            invoice.setStatus(debt_payments.domain.enums.InvoiceStatus.WRITTEN_OFF);
            invoiceProviderPort.updateInvoice(invoice);
        }

        // 4. Publicar evento para contabilidad 
        PortfolioWriteOffResponse writeOffToSend = buildEnrichedResponse(writeOff);
        accountingEventPublisher.publishWriteOffConfirmedEvent(writeOffToSend);

        // 5. Persistir el cambio de estado del castigo
        return writeOffPersistencePort.update(writeOff);
    }

    @Override
    public PortfolioWriteOff voidWriteOffConfirmation(Long writeOffId) {
        // 1. Obtener el agregado raíz
        PortfolioWriteOff writeOff = findWriteOffOrThrow(writeOffId);

        // 2. Ejecutar la lógica de negocio del dominio
        writeOff.voidConfirmation();

        // 3. Revertir los efectos secundarios (restaurar saldos de facturas)
        for (WriteOffDetail detail : writeOff.getDetails()) {
            InvoiceReplica invoice = findInvoiceOrThrow(detail.getInvoiceId());
            // ¡La clave está aquí! Restauramos el valor que guardamos.
            invoice.setPendingValue(invoice.getPendingValue() + detail.getAmountWrittenOff());
            invoice.setStatus(debt_payments.domain.enums.InvoiceStatus.PENDING); 
            invoiceProviderPort.updateInvoice(invoice);
        }

        // 4. Publicar evento de anulación para contabilidad 
        PortfolioWriteOffResponse writeOffToSend = buildEnrichedResponse(writeOff);
        accountingEventPublisher.publishWriteOffVoidedEvent(writeOffToSend);

        // 5. Persistir el cambio de estado del castigo
        return writeOffPersistencePort.update(writeOff);
    }

    @Override
    public Optional<PortfolioWriteOffResponse> findById(Long writeOffId) {
        // 1. Buscar el registro de castigo principal
        Optional<PortfolioWriteOff> writeOffOpt = writeOffPersistencePort.findById(writeOffId);
        
        // Si no se encuentra, devolver un Optional vacío
        if (writeOffOpt.isEmpty()) {
            return Optional.empty();
        }

        // 2. Si se encuentra, construir la respuesta enriquecida
        PortfolioWriteOff writeOff = writeOffOpt.get();
        PortfolioWriteOffResponse response = buildEnrichedResponse(writeOff);
        
        return Optional.of(response);
    }

    @Override
    public List<PortfolioWriteOffResponse> findByEnterpriseId(String enterpriseId) {
        // 1. Buscar todos los castigos para la empresa
        List<PortfolioWriteOff> writeOffs = writeOffPersistencePort.findByEnterpriseId(enterpriseId);
        
        // 2. Construir las respuestas enriquecidas para cada castigo
        return writeOffs.stream()
                .map(this::buildEnrichedResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca un castigo por su ID o lanza una excepción si no se encuentra.
     * 
     * @param writeOffId ID del castigo a buscar
     * @return El castigo encontrado
     * @throws RuntimeException si no se encuentra el castigo
     */
    private PortfolioWriteOff findWriteOffOrThrow(Long writeOffId) {
        return writeOffPersistencePort.findById(writeOffId)
                .orElseThrow(() -> new RuntimeException("PortfolioWriteOff not found with id: " + writeOffId));
    }

    /**
     * Busca una factura por su ID o lanza una excepción si no se encuentra.
     * 
     * @param invoiceId ID de la factura a buscar
     * @return La factura encontrada
     * @throws InvoiceNotFoundException si no se encuentra la factura
     */
    private InvoiceReplica findInvoiceOrThrow(Long invoiceId) {
        return invoiceProviderPort.findInvoiceById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + invoiceId));
    }

    /**
     * Construye un DTO de respuesta enriquecido para un castigo, incluyendo detalles de las facturas.
     * @param writeOff El castigo del cual construir la respuesta
     * @return El DTO de respuesta enriquecido
     */
    private PortfolioWriteOffResponse buildEnrichedResponse(PortfolioWriteOff writeOff) {
        // 1. Extraer todos los IDs de las facturas de los detalles del castigo
        List<Long> invoiceIds = writeOff.getDetails().stream()
                                        .map(WriteOffDetail::getInvoiceId)
                                        .toList();
        
        // 2. Obtener la información de todas esas facturas en una sola llamada
        Map<Long, InvoiceReplica> invoiceMap = invoiceProviderPort.findInvoicesByIds(invoiceIds).stream()
            .collect(Collectors.toMap(InvoiceReplica::getId, Function.identity()));

        // 3. Construir la lista de detalles de respuesta (WriteOffDetailResponse)
        List<WriteOffDetailResponse> detailResponses = writeOff.getDetails().stream()
            .map((WriteOffDetail detail) -> {
                InvoiceReplica invoice = invoiceMap.get(detail.getInvoiceId());
                // Manejar el caso donde la factura no se encuentra (aunque no debería pasar)
                if (invoice == null) {
                    return null; 
                }
                
                // Construir el resumen de la factura
                InvoiceSummaryResponse invoiceSummary = InvoiceSummaryResponse.builder()
                    .id(invoice.getId())
                    .factCode(invoice.getFactCode())
                    .totalValue(invoice.getTotalValue())
                    .pendingValue(detail.getAmountWrittenOff()) // ¡Importante! Mostramos el saldo que TENÍA
                    .expirationDate(invoice.getExpirationDate())
                    .build();
                
                // Construir el detalle del castigo
                return WriteOffDetailResponse.builder()
                    .amountWrittenOff(detail.getAmountWrittenOff())
                    .invoice(invoiceSummary)
                    .build();
            })
            .filter(java.util.Objects::nonNull) // Filtrar nulos si alguna factura no se encontró
            .collect(Collectors.toList());

        // 4. Construir y devolver el DTO de respuesta final
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
            .details(detailResponses)
            .build();
    }

    private String generateUniqueWriteOffCode() {
        return "CC-" + System.currentTimeMillis();
    }
}
