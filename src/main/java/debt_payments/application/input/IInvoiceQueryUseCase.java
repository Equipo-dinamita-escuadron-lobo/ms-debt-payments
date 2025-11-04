package debt_payments.application.input;

import java.util.List;

import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.model.Replica.InvoiceReplica;

public interface IInvoiceQueryUseCase {

    /**
     * Caso de uso para encontrar facturas con saldo pendiente de un cliente.
     * @param clientId El ID del cliente a consultar.
     * @return Lista de facturas con saldo pendiente.
     */
    List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId);

    /**
     * Caso de uso para encontrar facturas de un cliente por estado.
     * @param clientId El ID del cliente a consultar.
     * @param status El estado de las facturas a filtrar.
     * @return Lista de facturas del cliente con el estado especificado.
     */
    List<InvoiceReplica> findStatusInvoicesByClientId(Long clientId, InvoiceStatus status);

    /**
     * Caso de uso para encontrar facturas pendientes de una empresa.
     * @param enterpriseId El ID de la empresa a consultar.
     * @return Lista de facturas pendientes de la empresa.
     */
    List<InvoiceReplica> findPendingInvoicesByEnterpriseId(String enterpriseId);

    /**
     * Caso de uso para encontrar facturas de una empresa.
     * @param enterpriseId El ID de la empresa a consultar.
     * @return Lista de facturas de la empresa.
     */
    List<InvoiceReplica> findInvoicesByEnterpriseId(String enterpriseId);

    /**
     * Caso de uso para encontrar una factura por su id.
     * @param invoiceId El ID de la factura a consultar.
     * @return La factura encontrada o null si no existe.
     */
    InvoiceReplica findInvoiceById(Long invoiceId);
}
