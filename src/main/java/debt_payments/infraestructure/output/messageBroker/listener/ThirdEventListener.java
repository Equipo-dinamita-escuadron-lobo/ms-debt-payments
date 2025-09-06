package debt_payments.infraestructure.output.messageBroker.listener;

import java.io.IOException;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import debt_payments.infraestructure.output.messageBroker.adapter.ThirdPartyPersistenceAdapter;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.ThirdSyncDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThirdEventListener {

    private final ThirdPartyPersistenceAdapter thirdPartyPersistenceAdapter;

    public void handleThirdPartyEvent(
        EventDto<ThirdSyncDto> event, Channel channel, 
        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException
    {
        log.info("Received event type '{}' for third party with ID: {}", 
        event.getType(), 
        event.getData() != null ? event.getData().getThirdPartyId(): "NA" );

        try {
            if (event.getData() == null || event.getType() == null) {
                log.error("Event data or type is null. Rejecting message.");
                channel.basicNack(tag, false, false); // Enviar a DLQ
                return;
            }

            ThirdSyncDto dto = event.getData();

            switch (event.getType()) {
                case "CREATED":
                case "UPDATED":
                    thirdPartyPersistenceAdapter.saveOrUpdate(dto);
                    log.info("Successfully processed create/update for thirdPartyId: {}", dto.getThirdPartyId());
                    break;

                case "DELETED":
                    thirdPartyPersistenceAdapter.delete(dto.getThirdPartyId());
                    log.info("Successfully processed delete for thirdPartyId: {}", dto.getThirdPartyId());
                    break;

                default:
                    log.warn("Unknown event type '{}'. Message will be acknowledged and ignored.", event.getType());
            }

            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.error("An unexpected error occurred while processing thirdPartyId: {}. Error: {}", 
                    (event.getData() != null ? event.getData().getThirdPartyId() : "N/A"), 
                    e.getMessage(), e);
            
            channel.basicNack(tag, false, false);
        }
    }
}
