package debt_payments.domain.model.Replica;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.test.fixtures.TestFixtures;

public class InvoiceReplicaMarkPendingTest {

    @Test
    void markAsPendingWriteOff_setsStatusPendingWrittenOff() {
        var invoice = TestFixtures.invoiceReplicaDefault(10L);
        invoice.markAsPendingWriteOff();
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PENDING_WRITTEN_OFF);
    }
}
