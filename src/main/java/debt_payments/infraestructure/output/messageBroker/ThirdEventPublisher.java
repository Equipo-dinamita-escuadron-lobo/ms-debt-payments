package debt_payments.infraestructure.output.messageBroker;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.output.IThirdEventPublisher;
import debt_payments.infraestructure.config.RabbitThirdConfig;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.ThirdUsedEvenDto;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThirdEventPublisher implements IThirdEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    @Override
    public void publishThirdUsedEvent(Long thirdId, String enterpriseId) {
        ThirdUsedEvenDto thirdUsedEvenDto = new ThirdUsedEvenDto(thirdId, enterpriseId, 1);
        EventDto<ThirdUsedEvenDto> event = new EventDto<>("USED", thirdUsedEvenDto);
        log.info("Publishing third used event: {}", thirdId);

        rabbitTemplate.convertAndSend(RabbitThirdConfig.THIRD_USED_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }
}
