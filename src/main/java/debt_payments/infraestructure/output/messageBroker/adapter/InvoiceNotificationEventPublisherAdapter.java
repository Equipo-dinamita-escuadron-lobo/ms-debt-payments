package debt_payments.infraestructure.output.messageBroker.adapter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.output.IInvoiceNotificationEventPublisher;
import debt_payments.domain.model.InvoiceDueReminder;
import debt_payments.infraestructure.config.RabbitNotificationsConfig;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adapter class responsible for publishing invoice notification events to a RabbitMQ message broker.
 * It implements the IInvoiceNotificationEventPublisher interface.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceNotificationEventPublisherAdapter implements IInvoiceNotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    /**
     * @brief Publishes an invoice due reminder event to Notifications Exchange in RabbitMQ.
     * @param invoiceDueReminderEventDto Data transfer object containing details of the invoice due reminder event.
     */
    @Override
    public void publishInvoiceDueReminder(InvoiceDueReminder reminder) {
        InvoiceDueReminderEventDto invoiceDueReminderEventDto = new InvoiceDueReminderEventDto();
        invoiceDueReminderEventDto.setThirdPartyId(reminder.getThirdPartyId());
        invoiceDueReminderEventDto.setInvoiceDetails(reminder.getInvoiceDetails().stream().map(detail -> {
            var dto = new debt_payments.infraestructure.output.messageBroker.dto.InvoiceDetailEventDto();
            dto.setInvoiceId(detail.getInvoiceId());
            dto.setInvoiceCode(detail.getInvoiceCode());
            dto.setExpirationDate(detail.getExpirationDate());
            dto.setTotalAmount(detail.getTotalAmount());
            dto.setPendingValue(detail.getPendingValue());
            return dto;
        }).toList());
        EventDto<InvoiceDueReminderEventDto> event = new EventDto<>("INVOICE_DUE_REMINDER", invoiceDueReminderEventDto);
        log.info("Publishing invoice due reminder event for third party ID: {}", invoiceDueReminderEventDto.getThirdPartyId());

        rabbitTemplate.convertAndSend(RabbitNotificationsConfig.NOTIFICATIONS_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(java.util.Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }
}
