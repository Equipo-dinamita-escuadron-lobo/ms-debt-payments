package debt_payments.infraestructure.config.used;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Profile("!test")
public class RabbitCostCenterConfig {
    public static final String COSTCENTER_USED_EXCHANGE = "costcenter.used.exchange";
    public static final String COSTCENTER_USED_QUEUE = "costcenter.used.queue";

    //Statement EXCHANGES
    @Bean
    FanoutExchange costCenterUsedExchange() {
        return new FanoutExchange(COSTCENTER_USED_EXCHANGE, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    @Bean
    Queue costCenterUsedQueue() {
        return new Queue(COSTCENTER_USED_QUEUE, true);
    }

    @Bean
    Binding costCenterUsedBinding(){
        return BindingBuilder.bind(costCenterUsedQueue()).to(costCenterUsedExchange());
    }
}