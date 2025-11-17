package debt_payments.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.test.fixtures.TestFixtures;

@DisplayName("Unit tests for Receipt domain model")
public class ReceiptTest {

    @Test
    @DisplayName("isInvoicePayment returns true when type is INVOICE_PAYMENT")
    void isInvoicePaymentTrue() {
        var r = TestFixtures.receiptForInvoicePayment("ENT-1", 1L, 1L, "obs", List.of(TestFixtures.receiptDetail(1L, 100L)));
        assertThat(r.isInvoicePayment()).isTrue();
    }

    @Test
    @DisplayName("processInvoicePayments applies payments and fills detail invoice info")
    void processInvoicePaymentsAppliesPayments() throws Exception {
        var detail = TestFixtures.receiptDetail(1L, 200L);
        var receipt = TestFixtures.receiptForInvoicePayment("ENT-1", 1L, 1L, "obs", List.of(detail));

        InvoiceReplica inv = TestFixtures.invoiceReplicaWith(1L, 1000L, 500L, InvoiceStatus.PENDING);

        var modified = receipt.processInvoicePayments(id -> Optional.of(inv));

        assertThat(modified).containsExactly(inv);
        assertThat(inv.getTotalPay()).isEqualTo( (1000L - 500L) + 200L );
        assertThat(detail.getInvoiceCode()).isEqualTo(inv.getFactCode());
        assertThat(detail.getAccountingAccount()).isEqualTo(inv.getAccountingAccount());
    }

    @Test
    @DisplayName("voidReceipt reverses payments and sets status to VOIDED")
    void voidReceiptReversesPayments() throws Exception {
        var detail = TestFixtures.receiptDetail(2L, 150L);
        var receipt = TestFixtures.receiptForInvoicePayment("ENT-1", 1L, 1L, "obs", List.of(detail));

        InvoiceReplica inv = TestFixtures.invoiceReplicaWith(2L, 1000L, 850L, InvoiceStatus.PAID);

        var modified = receipt.voidReceipt("reason", id -> Optional.of(inv));

        assertThat(receipt.getStatus()).isEqualTo(ReceiptStatus.VOIDED);
        assertThat(modified).containsExactly(inv);
        assertThat(inv.getTotalPay()).isEqualTo( (1000L - 850L) - 150L );
    }

    @Test
    @DisplayName("assignReceiptCode throws if already assigned")
    void assignReceiptCodeThrowsWhenAlreadyAssigned() {
        var r = TestFixtures.receiptForDirectIncome("ENT-1", 1L, 1L, "obs", 1000L, 200L);
        r.setReceiptCode("RC-1");

        assertThatThrownBy(() -> r.assignReceiptCode("RC-2")).isInstanceOf(IllegalStateException.class);
    }
}
