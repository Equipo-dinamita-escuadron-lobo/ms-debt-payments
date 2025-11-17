package debt_payments.test.fixtures;

import java.time.LocalDate;
import java.util.List;

import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.WriteOffDetail;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.ReceiptDetail;

/**
 * Helpers reutilizables para pruebas unitarias.
 */
public final class TestFixtures {
    private TestFixtures() {}

    public static InvoiceReplica invoiceReplicaDefault(Long id) {
        return new InvoiceReplica(
                id,
                "F-" + id,
                1000L, // totalValue
                0L, // totalPay
                1L, // thirdId
                1000L, // pendingValue
                1000L, // accountingAccount
                LocalDate.now().minusDays(30), // creationDate
                LocalDate.now().plusDays(30), // expirationDate
                "ENT-1", // entId
                InvoiceStatus.PENDING,
                true
        );
    }

    public static InvoiceReplica invoiceReplicaWith(Long id, Long totalValue, Long pendingValue, InvoiceStatus status) {
        return new InvoiceReplica(
                id,
                "F-" + id,
                totalValue,
                totalValue - pendingValue, // totalPay
                1L,
                pendingValue,
                1000L,
                LocalDate.now().minusDays(30),
                LocalDate.now().plusDays(30),
                "ENT-1",
                status,
                true
        );
    }

    public static WriteOffDetail writeOffDetail(Long invoiceId, Long amount) {
        return WriteOffDetail.builder()
                .invoiceId(invoiceId)
                .amountWrittenOff(amount)
                .accountingAccount(1000L)
                .build();
    }

    public static WriteOffDetail writeOffDetailWithAccount(Long invoiceId, Long amount, Long accountingAccount) {
        return WriteOffDetail.builder()
                .invoiceId(invoiceId)
                .amountWrittenOff(amount)
                .accountingAccount(accountingAccount)
                .build();
    }

    public static ReceiptDetail receiptDetail(Long invoiceId, Long amountPaid) {
        return new ReceiptDetail(null, invoiceId, null, amountPaid, null);
    }

    public static Receipt receiptForInvoicePayment(String enterpriseId, Long thirdId, Long paymentMethodId, String observations, List<ReceiptDetail> details) {
        var r = new Receipt();
        r.setEnterpriseId(enterpriseId);
        r.setThirdPartyId(thirdId);
        r.setPaymentMethodId(paymentMethodId);
        r.setObservations(observations);
        r.setReceiptType(debt_payments.domain.enums.ReceiptType.INVOICE_PAYMENT);
        r.setDetails(details == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(details));
        r.setStatus(debt_payments.domain.model.ReceiptStatus.FINALIZED);
        return r;
    }

    public static Receipt receiptForDirectIncome(String enterpriseId, Long thirdId, Long paymentMethodId, String observations, Long totalAmount, Long ledgerAccountId) {
        var r = new Receipt();
        r.setEnterpriseId(enterpriseId);
        r.setThirdPartyId(thirdId);
        r.setPaymentMethodId(paymentMethodId);
        r.setObservations(observations);
        r.setReceiptType(debt_payments.domain.enums.ReceiptType.DIRECT_INCOME);
        r.setTotalAmount(totalAmount);
        r.setLedgerAccountId(ledgerAccountId);
        r.setStatus(debt_payments.domain.model.ReceiptStatus.FINALIZED);
        return r;
    }

    public static PortfolioWriteOff portfolioWriteOffWithDetails(String enterpriseId, Long thirdId, String justification, List<WriteOffDetail> details) {
        return PortfolioWriteOff.create(enterpriseId, thirdId, justification, details);
    }
}
