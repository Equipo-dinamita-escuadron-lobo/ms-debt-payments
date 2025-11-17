package debt_payments.infraestructure.output.messageBroker;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.output.ICostCenterEventPublisher;
import debt_payments.infraestructure.config.RabbitCostCenterConfig;
import debt_payments.infraestructure.output.messageBroker.dto.CostCenterUsedDto;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CostCenterEventPublisher implements ICostCenterEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    @Override
    public void publishCostCenterUsedEvent(Long costCenterId, String enterpriseId) {
        CostCenterUsedDto costCenterUsedDto = new CostCenterUsedDto(costCenterId, enterpriseId);
        EventDto<CostCenterUsedDto> event = new EventDto<>("USED", costCenterUsedDto);
        log.info("Publishing cost center used event: {}", costCenterId);

        rabbitTemplate.convertAndSend(RabbitCostCenterConfig.COSTCENTER_PAYMENTS_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }
    
}
