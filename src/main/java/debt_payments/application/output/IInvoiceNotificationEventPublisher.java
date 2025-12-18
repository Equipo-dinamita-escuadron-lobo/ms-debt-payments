package debt_payments.application.output;

import debt_payments.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;

public interface IInvoiceNotificationEventPublisher {
    void publishInvoiceDueReminder(InvoiceDueReminderEventDto event);
}
