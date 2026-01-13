package debt_payments.infraestructure.input.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import debt_payments.application.input.IInvoiceCommandUseCase;
import debt_payments.application.input.IInvoiceNotificationUseCase;
import debt_payments.application.input.IInvoiceQueryUseCase;
import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.input.rest.dto.request.UpdateDueDateRequest;
import debt_payments.infraestructure.input.rest.dto.response.InvoicePendingResponse;
import debt_payments.infraestructure.input.rest.mapper.IInvoiceRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @brief REST controller for managing invoices, including retrieval and updates 
 * Handles endpoints for fetching invoices by various criteria and updating invoice due dates
 * Also includes an endpoint to trigger invoice payment reminders
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class InvoiceController {
    private final IInvoiceQueryUseCase invoiceQueryUseCase;
    private final IInvoiceCommandUseCase invoiceCommandUseCase;
    private final IInvoiceRestMapper invoiceRestMapper;
    private final IInvoiceNotificationUseCase invoiceNotificationUseCase;

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoicePendingResponse> getInvoiceById(@PathVariable Long invoiceId) {
        var invoice = invoiceQueryUseCase.findInvoiceById(invoiceId);
        var response = invoiceRestMapper.toInvoicePendingResponse(invoice);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending/client/{clientId}")
    public ResponseEntity<List<InvoicePendingResponse>> getPendingInvoicesByClient(@PathVariable Long clientId) {
        var pendingInvoices = invoiceQueryUseCase.findStatusInvoicesByClientId(clientId, InvoiceStatus.PENDING);
        return ResponseEntity.ok(invoiceRestMapper.toInvoicePendingResponseList(pendingInvoices));
    }

    @PatchMapping("/{invoiceId}/due-date")
    public ResponseEntity<Void> updateDueDate( @PathVariable Long invoiceId, @Valid @RequestBody UpdateDueDateRequest request) {
        invoiceCommandUseCase.updateDueDate(invoiceId, request.getNewDueDate());
        return ResponseEntity.ok().build();
    }

    @GetMapping("invoices/by-enterprise/{enterpriseId}")
    public ResponseEntity<List<InvoicePendingResponse>> getInvoicesByEnterpriseId(@PathVariable String enterpriseId) {
        var pendingInvoices = invoiceQueryUseCase.findInvoicesByEnterpriseId(enterpriseId);
        return ResponseEntity.ok(invoiceRestMapper.toInvoicePendingResponseList(pendingInvoices));
    }

    @GetMapping("invoices/pending/by-enterprise/{enterpriseId}")
    public ResponseEntity<List<InvoicePendingResponse>> getPendingInvoicesByEnterpriseId(@PathVariable String enterpriseId) {
        var pendingInvoices = invoiceQueryUseCase.findPendingInvoicesByEnterpriseId(enterpriseId);
        return ResponseEntity.ok(invoiceRestMapper.toInvoicePendingResponseList(pendingInvoices));
    }

    @GetMapping("invoices/status/by-client/{clientId}/{status}")
    public ResponseEntity<List<InvoicePendingResponse>> getStatusInvoicesByClientId(
            @PathVariable Long clientId,
            @PathVariable InvoiceStatus status) {
        var invoices = invoiceQueryUseCase.findStatusInvoicesByClientId(clientId, status);
        return ResponseEntity.ok(invoiceRestMapper.toInvoicePendingResponseList(invoices));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<InvoicePendingResponse>> getExpiringInvoices(
            @RequestParam String enterpriseId, 
            @RequestParam(defaultValue = "5") int days) {

        List<InvoiceReplica> invoices = invoiceQueryUseCase.findExpiringInvoices(enterpriseId, days);
        
        List<InvoicePendingResponse> response = invoiceRestMapper.toInvoicePendingResponseList(invoices);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trigger-reminders")
    public ResponseEntity<String> triggerInvoiceReminders() {
        try {
            invoiceNotificationUseCase.processAndPublishDueInvoices(); 
            
            String message = "Proceso de envío de recordatorios iniciado. Revisa los logs de ms_debt_payments y ms_notifications para ver el progreso.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMessage = "Error al iniciar el proceso de recordatorios: " + e.getMessage();
            return ResponseEntity.internalServerError().body(errorMessage);
        }
    }
}
