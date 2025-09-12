package debt_payments.application.input;

import java.util.List;

import debt_payments.domain.model.Replica.InvoiceReplica;

public interface IInvoiceQueryUseCase {
    /**
     * Caso de uso para encontrar facturas con saldo pendiente de un cliente.
     * @param clientId El ID del cliente a consultar.
     * @return Lista de facturas con saldo pendiente.
     */
    List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId);
}
