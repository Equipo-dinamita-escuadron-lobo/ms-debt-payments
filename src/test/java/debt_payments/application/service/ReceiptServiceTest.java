package debt_payments.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IReceiptCommandPersistencePort;
import debt_payments.application.output.IReceiptQueryPersistencePort;
import debt_payments.application.input.IAccountingEventPublisher;
import debt_payments.domain.exception.ReceiptNotFoundException;
import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.Replica.InvoiceReplica;
import static debt_payments.test.fixtures.TestFixtures.*;
import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.model.ReceiptStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for ReceiptService")
public class ReceiptServiceTest {

    @Mock
    private IReceiptCommandPersistencePort receiptCommandPersistencePort;
    @Mock
    private IReceiptQueryPersistencePort receiptQueryPersistencePort;
    @Mock
    private IInvoiceProviderPort invoiceProviderPort;
    @Mock
    private IAccountingEventPublisher accountingEventPublisher;

    @InjectMocks
    private ReceiptService receiptService;

    @Test
    @DisplayName("createReceipt should process invoice payments and persist receipt")
    void shouldCreateReceiptAndProcessInvoices() {
        var detail = receiptDetail(1L, 100L);
        var receipt = receiptForInvoicePayment("ENT-1", 1L, 1L, "obs", List.of(detail));

        InvoiceReplica inv = invoiceReplicaWith(1L, 500L, 400L, InvoiceStatus.PENDING);

        when(invoiceProviderPort.findInvoiceById(1L)).thenReturn(Optional.of(inv));
        when(receiptCommandPersistencePort.save(any())).thenReturn(receipt);

        Receipt result = receiptService.createReceipt(receipt);

        assertThat(result).isEqualTo(receipt);
        verify(invoiceProviderPort).updateInvoice(inv);
        verify(accountingEventPublisher).publishReceiptCreatedEvent(receipt);
    }

    @Test
    @DisplayName("createReceipt wraps exceptions from invoice processing")
    void shouldWrapInvoiceProcessingExceptions() {
        var detail = receiptDetail(2L, 50L);
        var receipt = receiptForInvoicePayment("ENT-1", 1L, 1L, "obs", List.of(detail));

        when(invoiceProviderPort.findInvoiceById(2L)).thenThrow(new RuntimeException("remote error"));

        assertThatThrownBy(() -> receiptService.createReceipt(receipt)).isInstanceOf(IllegalStateException.class);
        verify(receiptCommandPersistencePort, never()).save(any());
        verify(accountingEventPublisher, never()).publishReceiptCreatedEvent(any());
    }

    @Test
    @DisplayName("voidReceipt should void and publish event when exists")
    void shouldVoidReceiptSuccessfully() {
        var detail = receiptDetail(3L, 50L);
        var receipt = receiptForInvoicePayment("ENT-1", 1L, 1L, "obs", List.of(detail));
        receipt.setId(10L);

        InvoiceReplica inv = invoiceReplicaWith(3L, 500L, 450L, InvoiceStatus.PAID);

        when(receiptQueryPersistencePort.findById(10L)).thenReturn(Optional.of(receipt));
        when(invoiceProviderPort.findInvoiceById(3L)).thenReturn(Optional.of(inv));
        when(receiptCommandPersistencePort.save(receipt)).thenReturn(receipt);

        Receipt result = receiptService.voidReceipt(10L, "customer request");

        assertThat(result.getStatus()).isEqualTo(ReceiptStatus.VOIDED);
        verify(invoiceProviderPort).updateInvoice(inv);
        verify(accountingEventPublisher).publishVoidReceiptEvent(receipt);
    }

    @Test
    @DisplayName("voidReceipt throws ReceiptNotFoundException when missing")
    void shouldThrowWhenVoidingMissingReceipt() {
        when(receiptQueryPersistencePort.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> receiptService.voidReceipt(999L, "r")).isInstanceOf(ReceiptNotFoundException.class);
        verify(accountingEventPublisher, never()).publishVoidReceiptEvent(any());
    }

    @Test
    @DisplayName("createReceipt should handle DIRECT_INCOME receipts and persist without invoice updates")
    void shouldCreateDirectIncomeReceiptAndPersist() {
        var receipt = receiptForDirectIncome("ENT-1", 2L, 2L, "direct", 1000L, 555L);

        when(receiptCommandPersistencePort.save(any())).thenReturn(receipt);

        Receipt result = receiptService.createReceipt(receipt);

        assertThat(result).isEqualTo(receipt);
        verify(receiptCommandPersistencePort).save(receipt);
        verify(accountingEventPublisher).publishReceiptCreatedEvent(receipt);
        verify(invoiceProviderPort, never()).updateInvoice(any());
    }

    @Test
    @DisplayName("createReceipt should propagate persistence exceptions and not publish event")
    void shouldPropagatePersistenceExceptionAndNotPublish() {
        var receipt = receiptForDirectIncome("ENT-1", 3L, 2L, "direct", 2000L, 777L);

        when(receiptCommandPersistencePort.save(any())).thenThrow(new RuntimeException("DB fail"));

        assertThatThrownBy(() -> receiptService.createReceipt(receipt)).isInstanceOf(RuntimeException.class);
        verify(accountingEventPublisher, never()).publishReceiptCreatedEvent(any());
    }
}
