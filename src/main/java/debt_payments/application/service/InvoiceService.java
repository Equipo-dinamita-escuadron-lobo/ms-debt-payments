package debt_payments.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import debt_payments.application.input.IInvoiceCommandUseCase;
import debt_payments.application.input.IInvoiceQueryUseCase;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.Replica.InvoiceReplica;
import lombok.RequiredArgsConstructor;

/**
 * @brief Service class for managing invoices.
 * This class implements both command and query use cases for invoices,
 * handling operations such as writing off invoices, updating due dates,
 * and retrieving invoices based on various criteria.
 */

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceCommandUseCase, IInvoiceQueryUseCase {

    private final IInvoiceProviderPort invoiceProviderPort;

    @Override
    public void writeOffInvoices(List<Long> invoiceIds) {
        // 1. Validación de entrada
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            return; 
        }

        // 2. Recuperar las facturas a procesar
        List<InvoiceReplica> invoicesToProcess = invoiceProviderPort.findInvoicesByIds(invoiceIds);

        // 3. Validación de negocio: Verificar que encontramos todas las facturas solicitadas
        if (invoicesToProcess.size() != invoiceIds.size()) {
            throw new InvoiceNotFoundException("One or more invoices could not be found.");
        }

        // 4. Lógica de negocio: Iterar y aplicar la regla de negocio del modelo de dominio
        for (InvoiceReplica invoice : invoicesToProcess) {
            invoice.writeOff();
            invoiceProviderPort.updateInvoice(invoice);
        }
    }

    @Override
    public void updateDueDate(Long invoiceId, LocalDate newDueDate) {
        InvoiceReplica invoice = invoiceProviderPort.findInvoiceById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("No se encontró la factura con ID: " + invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID) 
            throw new IllegalStateException("No se puede cambiar la fecha de vencimiento de una factura ya pagada.");

        invoice.setExpirationDate(newDueDate);
        invoice.validateDates();

        invoiceProviderPort.updateInvoice(invoice);
    }

    @Override
    public List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId) {
        return invoiceProviderPort.findPendingInvoicesByClientId(clientId);
    }

    @Override
    public List<InvoiceReplica> findInvoicesByEnterpriseId(String enterpriseId) {
        return invoiceProviderPort.findInvoicesByEnterpriseId(enterpriseId);
    }

    @Override
    public InvoiceReplica findInvoiceById(Long invoiceId) {
        return invoiceProviderPort.findInvoiceById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("No se encontró la factura con ID: " + invoiceId));
    }

    @Override
    public List<InvoiceReplica> findPendingInvoicesByEnterpriseId(String enterpriseId) {
        return invoiceProviderPort.findPendingInvoicesByEnterpriseId(enterpriseId);
    }

    @Override
    public List<InvoiceReplica> findStatusInvoicesByClientId(Long clientId, InvoiceStatus status) {
        return invoiceProviderPort.findStatusInvoicesByClientId(clientId, status);
    }

    @Override
    public List<InvoiceReplica> findExpiringInvoices(String enterpriseId, int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);

        // Buscamos solo las que están PENDIENTES (InvoiceStatus.PENDING)
        return invoiceProviderPort.findExpiringInvoices(enterpriseId, InvoiceStatus.PENDING, today, thresholdDate);
    }
}
