package debt_payments.infraestructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Profile("!test")
public class RabbitAccountingConfig {
    public static final String RECEIPT_EXCHANGE = "receipt.exchange";
    public static final String RECEIPT_ACCOUNTING_QUEUE = "receipt.accounting.queue";
    public static final String RECEIPT_ACCOUNTING_DLX = "receipt.accounting.dlx";
    public static final String RECEIPT_ACCOUNTING_DLQ = "receipt.accounting.dlq";
    public static final String RECEIPT_ACCOUNTING_RETRY_QUEUE = "receipt.accounting.retry.queue";

    // STATEMENT EXCHANGES
    @Bean
    FanoutExchange receiptExchange() {
        return new FanoutExchange(RECEIPT_EXCHANGE, true, false);
    }

    @Bean
    FanoutExchange receiptAccountingDlx() {
        return new FanoutExchange(RECEIPT_ACCOUNTING_DLX, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    @Bean
    Queue receiptAccountingQueue() {
        return QueueBuilder.durable(RECEIPT_ACCOUNTING_QUEUE)
                .withArgument("x-dead-letter-exchange", RECEIPT_ACCOUNTING_DLX).build();
    }

    @Bean
    Queue receiptAccountingDlq() {
        return QueueBuilder.durable(RECEIPT_ACCOUNTING_DLQ).build();
    }

    @Bean
    Queue receiptAccountingRetryQueue() {
        return QueueBuilder.durable(RECEIPT_ACCOUNTING_RETRY_QUEUE)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", RECEIPT_ACCOUNTING_DLX).build();
    }

    @Bean
    Binding receiptAccountingBinding() {
        return BindingBuilder.bind(receiptAccountingQueue()).to(receiptExchange());
    }

    @Bean
    Binding receiptAccountingDlqBinding() {
        return BindingBuilder.bind(receiptAccountingDlq()).to(receiptAccountingDlx());
    }

    @Bean
    Binding receiptAccountingRetryBinding() {
        return BindingBuilder.bind(receiptAccountingRetryQueue()).to(receiptAccountingDlx());
    }
}
