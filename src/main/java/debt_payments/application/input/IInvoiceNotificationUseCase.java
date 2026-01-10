package debt_payments.application.input;

public interface IInvoiceNotificationUseCase {
    public void processAndPublishDueInvoices();
}
