package debt_payments.infraestructure.output.messageBroker.listener;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import debt_payments.infraestructure.config.RabbitConfig;
import debt_payments.infraestructure.output.messageBroker.adapter.AccountPersistenceAdapter;
import debt_payments.infraestructure.output.messageBroker.dto.AccountSyncDto;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventListener {
    private final AccountPersistenceAdapter accountPersistenceAdapter;

    @RabbitListener(queues = RabbitConfig.ACCOUNT_PAYMENTS_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleAccountEvent(
        EventDto<AccountSyncDto> event, Channel channel, 
        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException
    {
        log.info("Received event type '{}' for account with ID: {}", 
        event.getType(), 
        event.getData() != null ? event.getData().getAccountId(): "NA" );

        try {
            if (event.getData() == null || event.getType() == null) {
                log.error("Event data or type is null. Rejecting message for account.");
                channel.basicNack(tag, false, false); // Enviar a DLQ
                return;
            }

            AccountSyncDto dto = event.getData();

            switch (event.getType()) {
                case "CREATED":
                case "UPDATED":
                    accountPersistenceAdapter.saveOrUpdate(dto);
                    log.info("Successfully processed create/update for accountId: {}", dto.getAccountId());
                    break;

                case "DELETED":
                    accountPersistenceAdapter.delete(dto.getAccountId());
                    log.info("Successfully processed delete for accountId: {}", dto.getAccountId());
                    break;

                default:
                    log.warn("Unknown event type '{}' for account. Message will be acknowledged and ignored.", event.getType());
            }

            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.error("An unexpected error occurred while processing accountId: {}. Error: {}", 
                    (event.getData() != null ? event.getData().getAccountId() : "N/A"), 
                    e.getMessage(), e);
            
            channel.basicNack(tag, false, true); 
        }
    }
}
