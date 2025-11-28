package debt_payments.application.service;

import static debt_payments.test.fixtures.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.Replica.InvoiceReplica;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for InvoiceService")
public class InvoiceServiceUnitTest {

    @Mock
    private IInvoiceProviderPort invoiceProviderPort;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    @DisplayName("writeOffInvoices should return when input is null or empty")
    void writeOffInvoices_nullOrEmpty_returns() {
        invoiceService.writeOffInvoices(null);
        invoiceService.writeOffInvoices(List.of());

        verifyNoInteractions(invoiceProviderPort);
    }

    @Test
    @DisplayName("writeOffInvoices should throw when some invoices missing")
    void writeOffInvoices_sizeMismatch_throws() {
        List<Long> ids = List.of(1L, 2L);
        when(invoiceProviderPort.findInvoicesByIds(ids)).thenReturn(List.of(invoiceReplicaDefault(1L)));

        assertThatThrownBy(() -> invoiceService.writeOffInvoices(ids))
                .isInstanceOf(InvoiceNotFoundException.class)
                .hasMessageContaining("could not be found");
    }

    @Test
    @DisplayName("writeOffInvoices should write off each invoice and call update")
    void writeOffInvoices_success_callsWriteOffAndUpdate() {
        var inv1 = invoiceReplicaWith(1L, 1000L, 200L, InvoiceStatus.PENDING);
        var inv2 = invoiceReplicaWith(2L, 500L, 100L, InvoiceStatus.PENDING);

        List<Long> ids = List.of(1L, 2L);
        when(invoiceProviderPort.findInvoicesByIds(ids)).thenReturn(List.of(inv1, inv2));

        invoiceService.writeOffInvoices(ids);

        // domain changes applied
        assertThat(inv1.getStatus()).isEqualTo(InvoiceStatus.WRITTEN_OFF);
        assertThat(inv1.getPendingValue()).isEqualTo(0L);

        assertThat(inv2.getStatus()).isEqualTo(InvoiceStatus.WRITTEN_OFF);
        assertThat(inv2.getPendingValue()).isEqualTo(0L);

        verify(invoiceProviderPort).updateInvoice(inv1);
        verify(invoiceProviderPort).updateInvoice(inv2);
    }

    @Test
    @DisplayName("updateDueDate should throw when invoice not found")
    void updateDueDate_notFound_throws() {
        when(invoiceProviderPort.findInvoiceById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.updateDueDate(99L, LocalDate.now()))
                .isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    @DisplayName("updateDueDate should throw when invoice is PAID")
    void updateDueDate_paid_throws() {
        var paid = invoiceReplicaWith(5L, 100L, 100L, InvoiceStatus.PAID);
        when(invoiceProviderPort.findInvoiceById(5L)).thenReturn(Optional.of(paid));

        assertThatThrownBy(() -> invoiceService.updateDueDate(5L, LocalDate.now().plusDays(5)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya pagada");
    }

    @Test
    @DisplayName("updateDueDate should set expiration, validate and update")
    void updateDueDate_success_updatesExpirationAndCallsUpdate() {
        var inv = invoiceReplicaDefault(10L);
        InvoiceReplica spyInv = spy(inv);
        when(invoiceProviderPort.findInvoiceById(10L)).thenReturn(Optional.of(spyInv));

        LocalDate newDate = LocalDate.now().plusDays(10);

        invoiceService.updateDueDate(10L, newDate);

        verify(spyInv).setExpirationDate(newDate);
        verify(spyInv).validateDates();
        verify(invoiceProviderPort).updateInvoice(spyInv);
    }

    @Test
    @DisplayName("find methods should delegate to provider")
    void findDelegations_delegates() {
        var list = List.of(invoiceReplicaDefault(1L));

        when(invoiceProviderPort.findPendingInvoicesByClientId(7L)).thenReturn(list);
        assertThat(invoiceService.findPendingInvoicesByClientId(7L)).isEqualTo(list);

        when(invoiceProviderPort.findInvoicesByEnterpriseId("ENT-A")).thenReturn(list);
        assertThat(invoiceService.findInvoicesByEnterpriseId("ENT-A")).isEqualTo(list);

        when(invoiceProviderPort.findPendingInvoicesByEnterpriseId("ENT-A")).thenReturn(list);
        assertThat(invoiceService.findPendingInvoicesByEnterpriseId("ENT-A")).isEqualTo(list);

        when(invoiceProviderPort.findStatusInvoicesByClientId(8L, InvoiceStatus.PENDING)).thenReturn(list);
        assertThat(invoiceService.findStatusInvoicesByClientId(8L, InvoiceStatus.PENDING)).isEqualTo(list);

        when(invoiceProviderPort.findInvoiceById(1L)).thenReturn(Optional.of(invoiceReplicaDefault(1L)));
        assertThat(invoiceService.findInvoiceById(1L)).isNotNull();
    }

    @Test
    @DisplayName("findInvoiceById should throw when missing")
    void findInvoiceById_missing_throws() {
        when(invoiceProviderPort.findInvoiceById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.findInvoiceById(123L))
                .isInstanceOf(InvoiceNotFoundException.class);
    }
}

