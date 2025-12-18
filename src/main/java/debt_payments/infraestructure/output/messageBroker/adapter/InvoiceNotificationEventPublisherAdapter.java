package debt_payments.infraestructure.output.messageBroker.adapter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.output.IInvoiceNotificationEventPublisher;
import debt_payments.infraestructure.config.RabbitNotificationsConfig;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceNotificationEventPublisherAdapter implements IInvoiceNotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    @Override
    public void publishInvoiceDueReminder(InvoiceDueReminderEventDto invoiceDueReminderEventDto) {
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
