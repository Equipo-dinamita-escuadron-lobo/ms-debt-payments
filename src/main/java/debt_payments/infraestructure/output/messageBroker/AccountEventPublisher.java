package debt_payments.infraestructure.output.messageBroker;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.output.IAccountEventPublisher;
import debt_payments.infraestructure.config.RabbitAccountConfig;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.used.AccountUsedEventDto;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher implements IAccountEventPublisher{

    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    @Override
    public void publishAccountCreatedEvent(Long account, String enterpriseId) {
        AccountUsedEventDto accountUsedEventDto = new AccountUsedEventDto(account, enterpriseId, "CODE");
        EventDto<AccountUsedEventDto> event = new EventDto<>("USED", accountUsedEventDto);
        log.info("Publishing account used event: {}", account);

        rabbitTemplate.convertAndSend(RabbitAccountConfig.ACCOUNT_USED_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }
    
}
