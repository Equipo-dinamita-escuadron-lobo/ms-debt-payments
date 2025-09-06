package debt_payments.infraestructure.output.messageBroker.listener;

import java.io.IOException;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import debt_payments.infraestructure.config.RabbitConfig;
import debt_payments.infraestructure.output.messageBroker.adapter.InvoicePersistenceAdapter;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceSyncDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceEventListener {
    private final InvoicePersistenceAdapter invoicePersistenceAdapter;

    @RabbitListener(queues = RabbitConfig.INVOICE_PAYMENTS_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleInvoiceEvent(
            EventDto<InvoiceSyncDto> event, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag,
            @Header("x-tenant-id") String tenantId) throws IOException {
        log.info("Received event type '{}' for invoice with ID: {}",
                event.getType(),
                event.getData() != null ? event.getData().getFactCode() : "NA");

        //TODO Revisar la parte de tenant
        // TenantContext.setCurrentTenant(tenantId);             
        log.info("Received event for tenant '{}'...", tenantId);

        try {
            if (event.getData() == null || event.getType() == null) {
                log.error("Event data or type is null. Rejecting message for invoice.");
                channel.basicNack(tag, false, false); // Enviar a DLQ
                return;
            }

            InvoiceSyncDto dto = event.getData();

            switch (event.getType()) {
                case "SALE":
                    invoicePersistenceAdapter.saveOrUpdate(dto);
                    log.info("Successfully processed sale for invoice factCode: {}", dto.getFactCode());
                    break;

                case "DELETED":
                    invoicePersistenceAdapter.delete(dto.getFactCode());
                    log.info("Successfully processed delete for invoice factCode: {}", dto.getFactCode());
                    break;

                default:
                    log.warn("Unknown event type '{}' for invoice. Message will be acknowledged and ignored.",
                            event.getType());
            }

            channel.basicAck(tag, false);
            //TODO Revisar la parte de tenant
            // TenantContext.clear(); // <-- Limpiar el contexto en un bloque finally

        } catch (Exception e) {
            log.error("An unexpected error occurred while processing invoice factCode: {}. Error: {}",
                    (event.getData() != null ? event.getData().getFactCode() : "N/A"),
                    e.getMessage(), e);


            channel.basicNack(tag, false, false);
        }
    }
}
