package debt_payments.application.input;

import java.util.List;

public interface IInvoiceCommandUseCase {
    /**
     * Marca una o varias facturas como castigadas.
     * @param invoiceIds Lista de IDs de las facturas a castigar.
     * @throws debt_payments.domain.exception.InvoiceNotFoundException si alguna de las facturas
     *         especificadas en la lista no se encuentra en el sistema.
     * @throws IllegalStateException si se intenta castigar una factura que ya ha sido pagada.
     */
    void writeOffInvoices(List<Long> invoiceIds);
}
