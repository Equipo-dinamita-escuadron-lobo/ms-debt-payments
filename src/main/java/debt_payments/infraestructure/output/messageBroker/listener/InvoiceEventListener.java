package debt_payments.infraestructure.output.messageBroker.listener;

import com.rabbitmq.client.Channel;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import debt_payments.domain.ports.IEventRecoveryActionPort;
import debt_payments.domain.ports.IMessageErrorHandlingPort;
import debt_payments.infraestructure.config.RabbitConfig;
import debt_payments.infraestructure.output.messageBroker.adapter.InvoicePersistenceAdapter;
import debt_payments.infraestructure.output.messageBroker.base.AbstractMessageListener;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceSyncDto;
import debt_payments.infraestructure.output.messageBroker.utils.JsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief RabbitMQ listener for invoice events 
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceEventListener extends AbstractMessageListener<EventDto<InvoiceSyncDto>> {
    private final InvoicePersistenceAdapter invoicePersistenceAdapter;
    private final IMessageErrorHandlingPort messageErrorHandlingPortImpl;
    private final IEventRecoveryActionPort<EventDto<InvoiceSyncDto>> productRecoveryActionPort;

    @PostConstruct
    private void init() {
        this.messageErrorHandlingPort = messageErrorHandlingPortImpl;
        this.eventRecoveryActionPort = productRecoveryActionPort;
    }

    /**
     * Handles incoming invoice events from the RabbitMQ queue.
     * @param message The raw AMQP message.
     * @param event The deserialized event data transfer object.
     * @param channel The RabbitMQ channel.
     * @param tag The delivery tag for message acknowledgment.
     */
    @RabbitListener(queues = RabbitConfig.INVOICE_PAYMENTS_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleInvoiceEvent(
            Message message,
            EventDto<InvoiceSyncDto> event, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        log.info("Received event type '{}' for invoice with ID: {}",
                event.getType(),
                event.getData() != null ? event.getData().getFactCode() : "NA");

        handleMessage(event, channel, tag);
    }

    /**
     * @brief Processes invoice events based on their type
     * @param event The invoice event to process
     */
    @Override
    protected void processEvent(EventDto<InvoiceSyncDto> event) {
        InvoiceSyncDto dto = event.getData();
        String factCode = dto.getFactCode().toString() != null ? dto.getFactCode().toString() : "N/A";

        try {

            switch (event.getType()) {
                case "SALE":
                    log.info("Processing SALE event for invoice factCode: {}", dto.getFactCode());
                    invoicePersistenceAdapter.saveOrUpdate(dto);
                    log.info("Successfully processed sale for invoice factCode: {}", dto.getFactCode());
                    break;

                case "DELETED":
                    log.info("Processing DELETE event for invoice factCode: {}", dto.getFactCode());
                    invoicePersistenceAdapter.delete(dto.getFactCode());
                    log.info("Successfully processed delete for invoice factCode: {}", dto.getFactCode());
                    break;

                default:
                    log.warn("Unknown event type '{}' for invoice. Message will be acknowledged and ignored.",
                            event.getType());
            }

        } catch (Exception e) {
            log.error("Database operation failed for invoice factCode: {}. Error: {}", factCode, e.getMessage());
            throw e;
        }
    }

    /**
     * @brief Validates invoice event data integrity
     * @param event Invoice event to validate
     * @return True if event is valid, false otherwise
     */
    @Override
    protected boolean isValidEvent(EventDto<InvoiceSyncDto> event) {
        if (event == null) {
            log.warn("Event is null");
            return false;
        }
        if (event.getData() == null) {
            log.warn("Event data is null");
            return false;
        }
        if (event.getType() == null) {
            log.warn("Event type is null");
            return false;
        }

        // Validar campos obligatorios
        InvoiceSyncDto data = event.getData();
        if (data.getFactCode() == null) {
            log.warn("FactCode is null");
            return false;
        }

        if (data.getAccountingAccount() == null) {
            log.warn("AccountingAccount is null");
            return false;
        }

        if (data.getEntId() == null) {
            log.warn("EnterpriseId is null");
            return false;
        }

        if (data.getExpirationDate() == null) {
            log.warn("ExpirationDate is null");
            return false;
        }

        if (data.getPendingValue() == null) {
            log.warn("PendingValue is null");
            return false;
        }

        if (data.getThirdId() == null) {
            log.warn("ThirdId is null");
            return false;
        }

        if (data.getTotalPay() == null) {
            log.warn("TotalPay is null");
            return false;
        }

        if (data.getTotalValue() == null) {
            log.warn("TotalValue is null");
            return false;
        }

        return true;
    }

    /**
     * @brief Returns the entity type for logging and error handling
     * @return Entity type string
     */
    @Override
    protected String getEntityType() {
        return "Invoice";
    }

    /**
     * @brief Extracts the event type from the invoice event
     * @param event The invoice event
     */
    @Override
    protected String extractEventType(EventDto<InvoiceSyncDto> event) {
        if (event == null) {
            return null;
        }

        return event.getType() != null ? event.getType() : null;
    }

    /**
     * @brief Converts invoice event to JSON string for error logging
     * @param event The invoice event
     */
    @Override
    protected String convertEventToJson(EventDto<InvoiceSyncDto> event) {
        if (event == null) {
            return "{\"error\": \"Event is null\"}";
        }

        if (event.getData() == null) {
            return "{\"error\": \"Event data is null\", \"eventType\": \"" +
                    (event.getType() != null ? event.getType() : "null") + "\"}";
        }

        return JsonUtils.toJsonWithNullHandling(event.getData());
    }
}
