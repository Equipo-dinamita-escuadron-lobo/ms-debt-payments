package debt_payments.application.input;

import java.time.LocalDate;
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

    /**
     * Actualiza la fecha de vencimiento de una factura específica.
     * @param invoiceId El ID de la factura a modificar (tipo Long, como en tu entidad).
     * @param newDueDate La nueva fecha de vencimiento.
     */
    void updateDueDate(Long invoiceId, LocalDate newDueDate);
}
