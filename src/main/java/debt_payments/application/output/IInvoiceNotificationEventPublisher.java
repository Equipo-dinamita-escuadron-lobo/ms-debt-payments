package debt_payments.application.output;

import debt_payments.domain.model.InvoiceDueReminder;

public interface IInvoiceNotificationEventPublisher {
    /**
     * @brief Publishes an invoice due reminder event.
     * @param event The event data transfer object containing invoice due reminder details.
     */
    void publishInvoiceDueReminder(InvoiceDueReminder event);
}
