package debt_payments.application.output;

import java.util.List;
import java.util.Optional;

import debt_payments.domain.model.Replica.InvoiceReplica;

public interface IInvoiceProviderPort {
    // Obtener el saldo pendiente de una factura por su ID
    Optional<Long> getInvoiceBalance(Long invoiceId);

    /**
     * Busca una factura por su ID.
     * @param invoiceId El ID de la factura a buscar.
     * @return Un Optional que contiene el objeto de dominio InvoiceReplica si se encuentra, o un Optional vacío si no.
     */
    Optional<InvoiceReplica> findInvoiceById(Long invoiceId);

    /**
     * Actualiza una factura en la base de datos.
     * @param invoice El objeto de dominio InvoiceReplica con los datos actualizados.
     */
    void updateInvoice(InvoiceReplica invoice);

    /**
     * Obtiene una lista de facturas con saldo pendiente para un cliente específico.
     *
     * @param clientId El ID del cliente.
     * @return Lista de modelos de dominio de InvoiceReplica.
     */
    List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId);
}
