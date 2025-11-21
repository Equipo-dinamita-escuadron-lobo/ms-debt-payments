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
public class RabbitPaymentMethodConfig {
    public static final String PAYMENTMETHOD_USED_EXCHANGE = "paymentmethod.used.exchange";
    public static final String PAYMENTMETHOD_USED_QUEUE = "paymentmethod.used.queue";

    //Statement EXCHANGES
    @Bean
    FanoutExchange paymentMethodUsedExchange() {
        return new FanoutExchange(PAYMENTMETHOD_USED_EXCHANGE, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    @Bean
    Queue paymentMethodUsedQueue() {
        return new Queue(PAYMENTMETHOD_USED_QUEUE, true);
    }

    @Bean
    Binding paymentMethodUsedBinding(){
        return BindingBuilder.bind(paymentMethodUsedQueue()).to(paymentMethodUsedExchange());
    }
}
