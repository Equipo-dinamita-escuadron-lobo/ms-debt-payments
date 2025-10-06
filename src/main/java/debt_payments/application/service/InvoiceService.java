package debt_payments.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import debt_payments.application.input.IInvoiceCommandUseCase;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.Replica.InvoiceReplica;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceCommandUseCase {

    private final IInvoiceProviderPort invoiceProviderPort;

    @Override
    public void writeOffInvoices(List<Long> invoiceIds) {
        // 1. Validación de entrada
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            return; 
        }

        List<InvoiceReplica> invoicesToProcess = invoiceProviderPort.findInvoicesByIds(invoiceIds);

        // 3. Validación de negocio: Verificar que encontramos todas las facturas solicitadas
        if (invoicesToProcess.size() != invoiceIds.size()) {
            throw new InvoiceNotFoundException("One or more invoices could not be found.");
        }

        // 4. Lógica de negocio: Iterar y aplicar la regla de negocio del modelo de dominio
        for (InvoiceReplica invoice : invoicesToProcess) {
            // La lógica de cómo castigar una factura está encapsulada en el propio objeto de dominio.
            // El servicio solo invoca el método.
            invoice.writeOff();
            
            invoiceProviderPort.updateInvoice(invoice);
        }
    }
    
}
