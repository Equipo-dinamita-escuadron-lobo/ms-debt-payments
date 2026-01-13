package debt_payments.infraestructure.input.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import debt_payments.application.input.IReceiptCommandUseCase;
import debt_payments.application.input.IReceiptQueryUseCase;
import debt_payments.domain.model.Receipt;
import debt_payments.infraestructure.input.rest.dto.request.ReceiptCreateRequest;
import debt_payments.infraestructure.input.rest.dto.request.VoidReceiptRequest;
import debt_payments.infraestructure.input.rest.dto.response.ReceiptResponse;
import debt_payments.infraestructure.input.rest.mapper.IReceiptRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @brief REST controller for managing receipts, including creation, voiding, and retrieval
 * Handles endpoints for creating receipts, voiding them, and fetching receipts by various criteria
 */

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class ReceiptController {
    private final IReceiptCommandUseCase receiptCommandUseCase;
    private final IReceiptQueryUseCase receiptQueryUseCase;
    private final IReceiptRestMapper receiptRestMapper;

    @PostMapping("/")
    public ResponseEntity<ReceiptResponse> createReceipt(@Valid @RequestBody ReceiptCreateRequest request) {
        Receipt domainModel = receiptRestMapper.toDomain(request);  //Convertir DTO de request a Modelo de Dominio
        Receipt createdReceipt = receiptCommandUseCase.createReceipt(domainModel);  //Llamar al caso de uso para ejecutar la lógica de negocio
        return ResponseEntity.status(HttpStatus.CREATED).body(receiptRestMapper.toResponse(createdReceipt));  //Convertir el resultado del Dominio a DTO de respuesta y devolverlo
    }

    @PutMapping("/{id}/void")
    public ResponseEntity<ReceiptResponse> voidReceipt(@PathVariable Long id, 
    @Valid @RequestBody VoidReceiptRequest request) {
        Receipt voidedReceipt = receiptCommandUseCase.voidReceipt(id, request.getReason()); 
        return ResponseEntity.ok(receiptRestMapper.toResponse(voidedReceipt));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptResponse> getReceiptById(@PathVariable Long id) {
        return receiptQueryUseCase.findById(id) 
                .map(receiptRestMapper::toResponse) 
                .map(ResponseEntity::ok) 
                .orElseGet(() -> ResponseEntity.notFound().build()); 
    }

    @GetMapping("/by-enterprise/{enterpriseId}")
    public ResponseEntity<List<ReceiptResponse>> getAllReceiptsByEnterprise(@PathVariable String enterpriseId) {
        List<Receipt> receipts = receiptQueryUseCase.findByEnterpriseId(enterpriseId);
        
        if (receipts.isEmpty()) 
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content si la lista está vacía

        return ResponseEntity.ok(receiptRestMapper.toResponseList(receipts));
    }

    @GetMapping("/by-invoice/{invoiceId}")
    public ResponseEntity<List<ReceiptResponse>> getAllReceiptsByInvoice(@PathVariable String invoiceId) {
        List<Receipt> receipts = receiptQueryUseCase.findByInvoiceId(invoiceId);

        if (receipts.isEmpty())
            return ResponseEntity.noContent().build(); 

        return ResponseEntity.ok(receiptRestMapper.toResponseList(receipts));
    }

    @GetMapping("/by-third/{thirdId}")
    public ResponseEntity<List<ReceiptResponse>> getAllReceiptsByThird(@PathVariable String thirdId) {
        List<Receipt> receipts = receiptQueryUseCase.findByThirdPartyId(thirdId);

        if (receipts.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(receiptRestMapper.toResponseList(receipts));
    }

}
