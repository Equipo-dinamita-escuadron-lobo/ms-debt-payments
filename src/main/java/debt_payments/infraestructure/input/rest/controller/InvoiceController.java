package debt_payments.infraestructure.input.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import debt_payments.application.input.IInvoiceQueryUseCase;
import debt_payments.infraestructure.input.rest.dto.response.InvoicePendingResponse;
import debt_payments.infraestructure.input.rest.mapper.IInvoiceRestMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class InvoiceController {
    private final IInvoiceQueryUseCase invoiceQueryUseCase;
    private final IInvoiceRestMapper invoiceRestMapper;

    @GetMapping("/pending/client/{clientId}")
    public ResponseEntity<List<InvoicePendingResponse>> getPendingInvoicesByClient(@PathVariable Long clientId) {
        var pendingInvoices = invoiceQueryUseCase.findPendingInvoicesByClientId(clientId);
        return ResponseEntity.ok(invoiceRestMapper.toInvoicePendingResponseList(pendingInvoices));
    }
}
