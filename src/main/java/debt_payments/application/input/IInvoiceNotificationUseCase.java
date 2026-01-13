package debt_payments.application.input;

public interface IInvoiceNotificationUseCase {
    /**
     * Process and publish due invoices.
     */
    public void processAndPublishDueInvoices();
}
