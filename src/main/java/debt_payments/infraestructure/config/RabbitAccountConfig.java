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
public class RabbitAccountConfig {
    public static final String ACCOUNT_USED_EXCHANGE = "account.used.exchange";
    public static final String ACCOUNT_USED_QUEUE = "account.used.queue";

    //Statement EXCHANGES
    @Bean
    FanoutExchange accountUsedExchange() {
        return new FanoutExchange(ACCOUNT_USED_EXCHANGE, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    @Bean
    Queue accountUsedQueue() {
        return new Queue(ACCOUNT_USED_QUEUE, true);
    }

    @Bean
    Binding accountUsedBinding(){
        return BindingBuilder.bind(accountUsedQueue()).to(accountUsedExchange());
    }
}
