package debt_payments.application.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import debt_payments.domain.enums.InvoiceStatus;
import debt_payments.domain.model.Replica.InvoiceReplica;

public interface IInvoiceProviderPort {
    // Obtener el saldo pendiente de una factura por su ID
    Optional<Long> getInvoiceBalance(Long invoiceId);

    /**
     * @brief Find an invoice by its ID.
     * @param invoiceId The ID of the invoice to find.
     * @return An Optional containing the InvoiceReplica domain object if found, or an empty Optional if not.
     */
    Optional<InvoiceReplica> findInvoiceById(Long invoiceId);

    /**
     * @brief Updates an invoice in the database.
     * @param invoice The InvoiceReplica domain object with updated data.
     */
    void updateInvoice(InvoiceReplica invoice);

    /**
     * @brief Obtains a list of invoices with pending balance for a specific client.
     * @param clientId The ID of the client.
     * @return A list of InvoiceReplica domain models.
     */
    List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId);

    /**
     * @brief Finds a list of invoices by their IDs.
     * This method is crucial for efficiently processing the write-off of multiple invoices.
     * @param invoiceIds List of invoice IDs to find.
     * @return A list of InvoiceReplica domain objects that were found.
     *         If an ID does not correspond to any invoice, it will simply not be included in the result list.
     */
    List<InvoiceReplica> findInvoicesByIds(List<Long> invoiceIds);

    /**
     * @brief Finds a list of invoices by the enterprise ID.
     * @param enterpriseId The ID of the enterprise.
     * @return A list of InvoiceReplica domain objects that belong to the specified enterprise.
     */
    List<InvoiceReplica> findInvoicesByEnterpriseId(String enterpriseId);

    /**
     * @brief Finds a list of pending invoices by the enterprise ID.
     * @param enterpriseId The ID of the enterprise.
     * @return A list of InvoiceReplica domain objects that are pending.
     */
    List<InvoiceReplica> findPendingInvoicesByEnterpriseId(String enterpriseId);

    /**
     * @brief Finds invoices by client ID and status.
     * @param clientId The ID of the client.
     * @param status The status of the invoices to filter.
     * @return A list of InvoiceReplica domain objects representing the invoices with the specified status for the client.
     */
    List<InvoiceReplica> findStatusInvoicesByClientId(Long clientId, InvoiceStatus status);

    /**
     * @brief Finds invoices of an enterprise with a specific status and whose expiration date
     * is within a given range. 
     * @param enterpriseId The ID of the enterprise.
     * @param status The status of the invoices to filter.
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A list of InvoiceReplica domain objects representing the invoices that meet the criteria.
     */
    List<InvoiceReplica> findExpiringInvoices(String enterpriseId, InvoiceStatus status, LocalDate startDate, LocalDate endDate);

    /**
     * @brief Finds invoices that expire on a specific date.
     * @param expirationLocalDate The target expiration date.
     * @return A list of InvoiceReplica domain objects representing the invoices that expire on the given date.
     */
    List<InvoiceReplica> findInvoicesByExpirationDate(LocalDate expirationLocalDate);
}
