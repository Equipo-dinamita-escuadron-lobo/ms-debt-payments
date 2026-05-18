package debt_payments.infraestructure.output.audit.builder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import debt_payments.domain.enums.ReceiptType;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.Receipt;
import debt_payments.infraestructure.output.audit.annotation.DocumentOperationType;

@Component
public class DocumentDataBuilder {

    public Map<String, Object> build(
            DocumentOperationType operationType,
            Object[] args,
            Object result) {
        return switch (operationType) {
            case CREATE -> buildForCreate(result, args);
            case APPROVE -> buildForApprove(result);
            case VOID -> buildForVoid(result, args);
            default -> Map.of();
        };
    }

    // Para crear
    private Map<String, Object> buildForCreate(Object result, Object[] args) {
        if (result instanceof Receipt receipt) {
            return buildReceiptCreate(receipt);
        }
        if (result instanceof PortfolioWriteOff writeOff) {
            return buildWriteOffCreate(writeOff);
        }
        return Map.of();
    }

    private Map<String, Object> buildReceiptCreate(Receipt receipt) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("receiptCode", receipt.getReceiptCode());
        header.put("receiptType", receipt.getReceiptType() != null ? receipt.getReceiptType().name() : null);
        header.put("paymentMethodId", receipt.getPaymentMethodId());
        header.put("thirdPartName", receipt.getThirdPartyId() != null ? "Cliente " + receipt.getThirdPartyId() : null);
        header.put("paymentMethodAccount", receipt.getPaymentMethodAccount());
        header.put("issueDate", receipt.getIssueDate());
        header.put("observations", receipt.getObservations());
        // Solo para ingreso directo
        if (receipt.getReceiptType() == ReceiptType.DIRECT_INCOME) {
            header.put("ledgerAccountId", receipt.getLedgerAccountId());
            header.put("centerCostId", receipt.getCenterCostId());
        }

        List<Map<String, Object>> details = List.of();
        if (receipt.isInvoicePayment() && receipt.getDetails() != null) {
            details = receipt.getDetails().stream()
                    .map(d -> {
                        Map<String, Object> line = new LinkedHashMap<>();
                        line.put("invoiceId", d.getInvoiceId());
                        line.put("invoiceCode", d.getInvoiceCode());
                        line.put("amountPaid", d.getAmountPaid());
                        line.put("accountingAccount", d.getAccountingAccount());
                        return line;
                    })
                    .toList();
        }

        Map<String, Object> totals = Map.of("totalAmount", receipt.getTotalAmount());

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("operationType", "CREATE");
        metadata.put("documentSubtype", receipt.getReceiptType() != null ? receipt.getReceiptType().name() : null);

        return Map.of("header", header, "details", details, "totals", totals, "metadata", metadata);
    }

    private Map<String, Object> buildWriteOffCreate(PortfolioWriteOff writeOff) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("code", writeOff.getCode());
        header.put("thirdId", writeOff.getThirdId());
        header.put("thirdPartName", writeOff.getThirdId() != null ? "Cliente " + writeOff.getThirdId() : null);
        header.put("justification", writeOff.getJustification());
        header.put("writeOffDate", writeOff.getWriteOffDate());
        header.put("status", writeOff.getStatus() != null ? writeOff.getStatus().name() : null);
        header.put("costCenterId", writeOff.getCostCenterId());
        header.put("debitAuxiliaryAccount", writeOff.getDebitAuxiliaryAccount());

        List<Map<String, Object>> details = List.of();
        if (writeOff.getDetails() != null) {
            details = writeOff.getDetails().stream()
                    .map(d -> {
                        Map<String, Object> line = new LinkedHashMap<>();
                        line.put("invoiceId", d.getInvoiceId());
                        line.put("amountWrittenOff", d.getAmountWrittenOff());
                        line.put("accountingAccount", d.getAccountingAccount());
                        return line;
                    })
                    .toList();
        }

        Map<String, Object> totals = Map.of("totalAmount", writeOff.getTotalAmount());

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("operationType", "CREATE");
        return Map.of("header", header, "details", details, "totals", totals, "metadata", metadata);
    }

    private Map<String, Object> buildForApprove(Object result) {
        if (!(result instanceof PortfolioWriteOff writeOff))
            return Map.of();

        // header: solo el cambio de estado
        Map<String, Object> headerChanges = Map.of(
                "status", Map.of("before", "PENDING_CONFIRMATION", "after", "CONFIRMED"));

        // details: diff por factura — el dominio ya corrió writeOff() en cada invoice
        // en este punto las invoices ya tienen pendingValue = 0 y status = WRITTEN_OFF
        List<Map<String, Object>> details = List.of();
        if (writeOff.getDetails() != null) {
            details = writeOff.getDetails().stream()
                    .map(d -> {
                        Map<String, Object> line = new LinkedHashMap<>();
                        line.put("invoiceId", d.getInvoiceId());
                        line.put("changes", Map.of(
                                "invoiceStatus", Map.of("before", "PENDING_WRITTEN_OFF", "after", "WRITTEN_OFF"),
                                "pendingValue", Map.of("before", d.getAmountWrittenOff(), "after", 0)));
                        return line;
                    })
                    .toList();
        }

        Map<String, Object> totals = Map.of("totalAmount", writeOff.getTotalAmount());
        Map<String, Object> metadata = Map.of("operationType", "APPROVE");

        return Map.of(
                "header", Map.of("changes", headerChanges),
                "details", details,
                "totals", totals,
                "metadata", metadata);
    }

    private Map<String, Object> buildForVoid(Object result, Object[] args) {
        if (result instanceof Receipt receipt) {
            return buildReceiptVoid(receipt);
        }
        if (result instanceof PortfolioWriteOff writeOff) {
            return buildWriteOffVoid(writeOff);
        }
        return Map.of();
    }

    private Map<String, Object> buildReceiptVoid(Receipt receipt) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("receiptCode", receipt.getReceiptCode());
        header.put("receiptType", receipt.getReceiptType() != null ? receipt.getReceiptType().name() : null);
        header.put("thirdPartyId", receipt.getThirdPartyId());
        header.put("thirdPartName", receipt.getThirdPartyId() != null ? "Cliente " + receipt.getThirdPartyId() : null);
        header.put("status", "VOIDED");
        header.put("issueDate", receipt.getIssueDate());
        header.put("voidDate", receipt.getVoidDate());
        header.put("voidReasonDescription", receipt.getVoidReasonDescription());

        // Para INVOICE_PAYMENT el details muestra los pagos revertidos
        List<Map<String, Object>> details = List.of();
        if (receipt.isInvoicePayment() && receipt.getDetails() != null) {
            details = receipt.getDetails().stream()
                    .map(d -> {
                        Map<String, Object> line = new LinkedHashMap<>();
                        line.put("invoiceId", d.getInvoiceId());
                        line.put("invoiceCode", d.getInvoiceCode());
                        line.put("amountReversed", d.getAmountPaid());
                        line.put("accountingAccount", d.getAccountingAccount());
                        return line;
                    })
                    .toList();
        }

        Map<String, Object> totals = Map.of("totalAmountReversed", receipt.getTotalAmount());

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("operationType", "VOID");
        metadata.put("documentSubtype",
                receipt.getReceiptType() != null ? receipt.getReceiptType().name() : null);
        metadata.put("affectsInvoices", receipt.isInvoicePayment());

        return Map.of("header", header, "details", details, "totals", totals, "metadata", metadata);
    }

    private Map<String, Object> buildWriteOffVoid(PortfolioWriteOff writeOff) {
        // header con campos planos + cambios de estado
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("code", writeOff.getCode());
        header.put("thirdId", writeOff.getThirdId());
        header.put("writeOffDate", writeOff.getWriteOffDate());
        header.put("changes", Map.of(
                "status", Map.of("before", "CONFIRMED", "after", "VOIDED")));

        // details: diff de reversión por factura
        List<Map<String, Object>> details = List.of();
        if (writeOff.getDetails() != null) {
            details = writeOff.getDetails().stream()
                    .map(d -> {
                        Map<String, Object> line = new LinkedHashMap<>();
                        line.put("invoiceId", d.getInvoiceId());
                        line.put("changes", Map.of(
                                "invoiceStatus", Map.of("before", "WRITTEN_OFF", "after", "PENDING"),
                                "pendingValue", Map.of("before", 0, "after", d.getAmountWrittenOff())));
                        return line;
                    })
                    .toList();
        }

        Map<String, Object> totals = Map.of("totalAmountRestored", writeOff.getTotalAmount());
        Map<String, Object> metadata = Map.of(
                "operationType", "VOID");

        return Map.of("header", header, "details", details, "totals", totals, "metadata", metadata);
    }
}
