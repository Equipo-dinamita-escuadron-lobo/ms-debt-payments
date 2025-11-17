package debt_payments.infraestructure.config;

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
public class RabbitThirdConfig {
    public static final String THIRD_USED_EXCHANGE = "third.used.exchange";
    public static final String THIRD_USED_QUEUE = "third.used.queue";

    //Statement EXCHANGES
    @Bean
    FanoutExchange thirdUsedExchange() {
        return new FanoutExchange(THIRD_USED_EXCHANGE, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    @Bean
    Queue thirdUsedQueue() {
        return new Queue(THIRD_USED_QUEUE, true);
    }

    @Bean
    Binding thirdUsedBinding(){
        return BindingBuilder.bind(thirdUsedQueue()).to(thirdUsedExchange());
    }
}
