package debt_payments.infraestructure.input.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import debt_payments.application.input.IInvoiceCommandUseCase;
import debt_payments.application.input.IInvoiceQueryUseCase;
import debt_payments.infraestructure.input.rest.dto.request.UpdateDueDateRequest;
import debt_payments.infraestructure.input.rest.dto.response.InvoicePendingResponse;
import debt_payments.infraestructure.input.rest.mapper.IInvoiceRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class InvoiceController {
    private final IInvoiceQueryUseCase invoiceQueryUseCase;
    private final IInvoiceCommandUseCase invoiceCommandUseCase;
    private final IInvoiceRestMapper invoiceRestMapper;

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoicePendingResponse> getInvoiceById(@PathVariable Long invoiceId) {
        var invoice = invoiceQueryUseCase.findInvoiceById(invoiceId);
        var response = invoiceRestMapper.toInvoicePendingResponse(invoice);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending/client/{clientId}")
    public ResponseEntity<List<InvoicePendingResponse>> getPendingInvoicesByClient(@PathVariable Long clientId) {
        var pendingInvoices = invoiceQueryUseCase.findPendingInvoicesByClientId(clientId);
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
}
