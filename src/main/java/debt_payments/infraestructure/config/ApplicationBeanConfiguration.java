package debt_payments.infraestructure.config;

import debt_payments.application.output.IInvoiceNotificationEventPublisher;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IMessageProcessingErrorPersistencePort;
import debt_payments.application.service.InvoiceNotificationService;
import debt_payments.application.service.InvoiceService;
import debt_payments.application.service.MessageProcessingErrorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    InvoiceService invoiceService(IInvoiceProviderPort invoices) {
        return new InvoiceService(invoices);
    }

    @Bean
    InvoiceNotificationService invoiceNotificationService(IInvoiceProviderPort invoices,
            IInvoiceNotificationEventPublisher publisher) {
        return new InvoiceNotificationService(invoices, publisher);
    }

    @Bean
    MessageProcessingErrorService messageProcessingErrorService(IMessageProcessingErrorPersistencePort persistence) {
        return new MessageProcessingErrorService(persistence);
    }
}
