package debt_payments.infraestructure.output.messageBroker;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.input.IAccountingEventPublisher;
import debt_payments.domain.model.Receipt;
import debt_payments.infraestructure.config.RabbitAccountingConfig;
import debt_payments.infraestructure.input.rest.dto.response.PortfolioWriteOffResponse;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.ReceiptEventDto;
import debt_payments.infraestructure.output.messageBroker.mapper.IReceiptEventMapper;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountingEventPublisher implements IAccountingEventPublisher {
    private final IReceiptEventMapper receiptEventMapper;
    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    @Override
    public void publishReceiptCreatedEvent(Receipt receipt) {
        ReceiptEventDto receiptEventDto = receiptEventMapper.toEventDto(receipt);
        EventDto<ReceiptEventDto> event = new EventDto<>("RECEIPT_CREATED", receiptEventDto);
        log.info("Publishing receipt created event: {}", receiptEventDto.getReceiptCode());

        rabbitTemplate.convertAndSend(RabbitAccountingConfig.RECEIPT_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }

    @Override
    public void publishVoidReceiptEvent(Receipt receipt) {
        ReceiptEventDto receiptEventDto = receiptEventMapper.toEventDto(receipt);
        EventDto<ReceiptEventDto> event = new EventDto<>("RECEIPT_VOIDED", receiptEventDto);
        log.info("Publishing receipt voided event: {}", receiptEventDto.getReceiptCode());

        rabbitTemplate.convertAndSend(RabbitAccountingConfig.RECEIPT_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }

    @Override
    public void publishWriteOffConfirmedEvent(PortfolioWriteOffResponse writeOffResponse) {
        EventDto<PortfolioWriteOffResponse> event = new EventDto<>("WRITEOFF_CONFIRMED", writeOffResponse);
        log.info("Publishing write-off confirmed event: {}", writeOffResponse.getCode());

        rabbitTemplate.convertAndSend(RabbitAccountingConfig.WRITEOFF_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }

    @Override
    public void publishWriteOffVoidedEvent(PortfolioWriteOffResponse writeOffResponse) {
        EventDto<PortfolioWriteOffResponse> event = new EventDto<>("WRITEOFF_VOIDED", writeOffResponse);
        log.info("Publishing write-off voided event: {}", writeOffResponse.getCode());

        rabbitTemplate.convertAndSend(RabbitAccountingConfig.WRITEOFF_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }
    
}
