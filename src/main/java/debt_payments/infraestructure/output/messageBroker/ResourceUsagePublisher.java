package debt_payments.infraestructure.output.messageBroker;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.output.IResourceUsageNotifierPort;
import debt_payments.domain.model.used.CostCenterUsedNotification;
import debt_payments.domain.model.used.PaymentMethodUsedNotification;
import debt_payments.domain.model.used.ThirdPartyUsedNotification;
import debt_payments.domain.ports.ResourceUsageNotification;
import debt_payments.infraestructure.config.used.RabbitCostCenterConfig;
import debt_payments.infraestructure.config.used.RabbitPaymentMethodConfig;
import debt_payments.infraestructure.config.used.RabbitThirdConfig;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.used.CostCenterUsedDto;
import debt_payments.infraestructure.output.messageBroker.dto.used.PaymentMethodUsedEventDto;
import debt_payments.infraestructure.output.messageBroker.dto.used.ThirdUsedEvenDto;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Implementation of the Resource Usage Notifier using RabbitMQ.
 * This class is responsible for notifying the usage of various resources to the message broker.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceUsagePublisher implements IResourceUsageNotifierPort {

    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    @Override
    public void notifyAll(List<ResourceUsageNotification> notifications) {
        notifications.forEach(this::dispatch);
    }

    private void dispatch(ResourceUsageNotification notification) {
        if (notification instanceof ThirdPartyUsedNotification) {
            handle((ThirdPartyUsedNotification) notification);
        } else if (notification instanceof CostCenterUsedNotification) {
            handle((CostCenterUsedNotification) notification);
        }else if (notification instanceof PaymentMethodUsedNotification) {
            handle((PaymentMethodUsedNotification) notification);
        }
        else {
            log.warn("No handler found for notification type: {}", notification.getClass().getSimpleName());
        }
    }


    private void handle(ThirdPartyUsedNotification notification) {
        ThirdUsedEvenDto dto = new ThirdUsedEvenDto(notification.getThirdId(), notification.getEnterpriseId(), 1);
        EventDto<ThirdUsedEvenDto> event = new EventDto<>("USED", dto);
        log.info("Publishing third used event: {}", notification.getThirdId());

        rabbitTemplate.convertAndSend(RabbitThirdConfig.THIRD_USED_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of("x-jwt-token", jwtUtils.getToken()));
            return message;
        });
    }

    private void handle(CostCenterUsedNotification notification) {
        CostCenterUsedDto dto = new CostCenterUsedDto(notification.getCostCenterId(), notification.getEnterpriseId(), 1);
        EventDto<CostCenterUsedDto> event = new EventDto<>("USED", dto);
        log.info("Publishing cost center used event: {}", notification.getCostCenterId());

        rabbitTemplate.convertAndSend(RabbitCostCenterConfig.COSTCENTER_USED_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of("x-jwt-token", jwtUtils.getToken()));
            return message;
        });
    }

    private void handle(PaymentMethodUsedNotification notification) {
        PaymentMethodUsedEventDto dto = new PaymentMethodUsedEventDto(notification.getPaymentMethodId(), notification.getEnterpriseId(), 1);
        EventDto<PaymentMethodUsedEventDto> event = new EventDto<>("USED", dto);
        log.info("Publishing payment method used event: {}", notification.getPaymentMethodId());

        rabbitTemplate.convertAndSend(RabbitPaymentMethodConfig.PAYMENTMETHOD_USED_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of("x-jwt-token", jwtUtils.getToken()));
            return message;
        });
    }

}