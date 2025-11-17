package debt_payments.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.enums.WriteOffStatus;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.test.fixtures.TestFixtures;

public class PortfolioWriteOffProcessVoidanceTest {

    @Test
    void processVoidance_reversesWriteOffOnInvoices() {
        InvoiceReplica invoice = TestFixtures.invoiceReplicaWith(1L, 1000L, 0L, InvoiceStatus.WRITTEN_OFF);

        var detail = TestFixtures.writeOffDetail(1L, 500L);
        PortfolioWriteOff writeOff = TestFixtures.portfolioWriteOffWithDetails("ENT-1", 1L, "just", List.of(detail));
        writeOff.setStatus(WriteOffStatus.VOIDED);

        List<InvoiceReplica> result = writeOff.processVoidance(id -> Optional.of(invoice));

        assertThat(result).hasSize(1);
        InvoiceReplica modified = result.get(0);
        assertThat(modified.getStatus()).isEqualTo(InvoiceStatus.PENDING);
        assertThat(modified.getPendingValue()).isEqualTo(500L);
    }

    @Test
    void processVoidance_missingInvoice_throwsInvoiceNotFoundException() {
        var detail = TestFixtures.writeOffDetail(99L, 200L);
        PortfolioWriteOff writeOff = TestFixtures.portfolioWriteOffWithDetails("ENT-1", 1L, "just", List.of(detail));
        writeOff.setStatus(WriteOffStatus.VOIDED);

        assertThatThrownBy(() -> writeOff.processVoidance(id -> Optional.empty()))
                .isInstanceOf(InvoiceNotFoundException.class);
    }
}
