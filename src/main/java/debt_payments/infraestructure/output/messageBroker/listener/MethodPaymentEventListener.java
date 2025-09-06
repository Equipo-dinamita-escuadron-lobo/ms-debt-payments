package debt_payments.infraestructure.output.messageBroker.listener;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import debt_payments.infraestructure.config.RabbitConfig;
import debt_payments.infraestructure.output.messageBroker.adapter.MethodPaymentPersistenceAdapter;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.MethodPaymentSyncDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MethodPaymentEventListener {
    private final MethodPaymentPersistenceAdapter methodPaymentPersistenceAdapter;

    @RabbitListener(queues = RabbitConfig.METHOD_PAYMENT_PAYMENTS_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleMethodPaymentEvent(
        EventDto<MethodPaymentSyncDto> event, Channel channel, 
        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException
    {
        log.info("Received event type '{}' for method payment with ID: {}", 
        event.getType(), 
        event.getData() != null ? event.getData().getMethodPaymentId(): "NA" );

        try {
            if (event.getData() == null || event.getType() == null) {
                log.error("Event data or type is null. Rejecting message for method payment.");
                channel.basicNack(tag, false, false); // Enviar a DLQ
                return;
            }

            MethodPaymentSyncDto dto = event.getData();

            switch (event.getType()) {
                case "CREATED":
                case "UPDATED":
                    methodPaymentPersistenceAdapter.saveOrUpdate(dto);
                    log.info("Successfully processed create/update for methodPaymentId: {}", dto.getMethodPaymentId());
                    break;

                case "DELETED":
                    methodPaymentPersistenceAdapter.delete(dto.getMethodPaymentId());
                    log.info("Successfully processed delete for methodPaymentId: {}", dto.getMethodPaymentId());
                    break;

                default:
                    log.warn("Unknown event type '{}' for method payment. Message will be acknowledged and ignored.", event.getType());
            }

            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.error("An unexpected error occurred while processing methodPaymentId: {}. Error: {}", 
                    (event.getData() != null ? event.getData().getMethodPaymentId() : "N/A"), 
                    e.getMessage(), e);
            
            channel.basicNack(tag, false, true);
        }
    }
}
