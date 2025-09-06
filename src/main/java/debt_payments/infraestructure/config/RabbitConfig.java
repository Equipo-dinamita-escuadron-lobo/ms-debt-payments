package debt_payments.infraestructure.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;

@Configuration
@Slf4j
@Profile("!test")
public class RabbitConfig {
    //Constants for third parties
    public static final String THIRD_EXCHANGE = "third.exchange";
    public static final String THIRD_PAYMENTS_QUEUE = "third.payments.queue";

    public static final String THIRD_PAYMENTS_DLX = "third.payments.dlx";
    public static final String THIRD_PAYMENTS_DLQ = "third.payments.dlq";
    public static final String THIRD_PAYMENTS_RETRY_QUEUE = "third.payments.retry.queue";

    //Constants for invoice
    public static final String INVOICE_EXCHANGE = "invoice.exchange";
    public static final String INVOICE_PAYMENTS_QUEUE = "invoice.payments.queue";
    public static final String INVOICE_PAYMENTS_DLX = "invoice.payments.dlx";
    public static final String INVOICE_PAYMENTS_DLQ = "invoice.payments.dlq";
    public static final String INVOICE_PAYMENTS_RETRY_QUEUE = "invoice.payments.retry.queue";

    //Constants for account
    public static final String ACCOUNT_EXCHANGE = "account.exchange";
    public static final String ACCOUNT_PAYMENTS_QUEUE = "account.payments.queue";
    public static final String ACCOUNT_PAYMENTS_DLX = "account.payments.dlx";
    public static final String ACCOUNT_PAYMENTS_DLQ = "account.payments.dlq";
    public static final String ACCOUNT_PAYMENTS_RETRY_QUEUE = "account.payments.retry.queue";

    //Constants for method payment
    public static final String METHOD_PAYMENT_EXCHANGE = "method_payment.exchange";
    public static final String METHOD_PAYMENT_PAYMENTS_QUEUE = "method_payment.payments.queue";
    public static final String METHOD_PAYMENT_PAYMENTS_DLX = "method_payment.payments.dlx";
    public static final String METHOD_PAYMENT_PAYMENTS_DLQ = "method_payment.payments.dlq";
    public static final String METHOD_PAYMENT_PAYMENTS_RETRY_QUEUE = "method_payment.payments.retry.queue";

    //Jackson2JsonMessageConverter
    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // STATEMENT EXCHANGES
    // Primary and message exchange dead for third parties
    @Bean
    FanoutExchange thirdExchange() {
        return new FanoutExchange(THIRD_EXCHANGE, true, false);
    }

    @Bean
    FanoutExchange thirdPaymentsDlx() {
        return new FanoutExchange(THIRD_PAYMENTS_DLX, true, false);
    }

    // Primary and message exchange dead for third invoices
    @Bean
    FanoutExchange invoiceExchange() {
        return new FanoutExchange(INVOICE_EXCHANGE, true, false);
    }

    @Bean
    FanoutExchange invoicePaymentsDlx() {
        return new FanoutExchange(INVOICE_PAYMENTS_DLX, true, false);
    }

    // Primary and message exchange dead for accounts
    @Bean
    FanoutExchange accountExchange() {
        return new FanoutExchange(ACCOUNT_EXCHANGE, true, false);
    }

    @Bean
    FanoutExchange accountPaymentsDlx() {
        return new FanoutExchange(ACCOUNT_PAYMENTS_DLX, true, false);
    }

    // Primary and message exchange dead for method payment
    @Bean
    FanoutExchange methodPaymentExchange() {
        return new FanoutExchange(METHOD_PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    FanoutExchange methodPaymentPaymentsDlx() {
        return new FanoutExchange(METHOD_PAYMENT_PAYMENTS_DLX, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    // Third party Queues and Bindings
    Queue thirdPaymentsQueue() {
        return QueueBuilder.durable(THIRD_PAYMENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", THIRD_PAYMENTS_DLX)
                .build();
    }

    Queue thirdPaymentsDlq() {
        return QueueBuilder.durable(THIRD_PAYMENTS_DLQ).build();
    }

    Queue thirdPaymentsRetryQueue() {
        return QueueBuilder.durable(THIRD_PAYMENTS_RETRY_QUEUE)
                .withArgument("x-message-ttl", 60000)
                .withArgument("x-dead-letter-exchange", THIRD_PAYMENTS_DLX)
                .build();
    }

    Binding thirdPaymentsBinding() {
        return BindingBuilder.bind(thirdPaymentsQueue()).to(thirdExchange());
    }

    Binding thirdPaymentsDlqBinding() {
        return BindingBuilder.bind(thirdPaymentsDlq()).to(thirdPaymentsDlx());
    }

    Binding thirdPaymentsRetryBinding() {
        return BindingBuilder.bind(thirdPaymentsRetryQueue()).to(thirdPaymentsDlx());
    }

    // Invoice Queues and Bindings
    @Bean
    Queue invoicePaymentsQueue() {
        return QueueBuilder.durable(INVOICE_PAYMENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", INVOICE_PAYMENTS_DLX)
                .build();
    }

    @Bean
    Queue invoicePaymentsDlq() {
        return QueueBuilder.durable(INVOICE_PAYMENTS_DLQ).build();
    }

    @Bean
    Queue invoicePaymentsRetryQueue() {
        return QueueBuilder.durable(INVOICE_PAYMENTS_RETRY_QUEUE)
                .withArgument("x-message-ttl", 60000) // 1 minuto de espera para reintento
                .withArgument("x-dead-letter-exchange", INVOICE_PAYMENTS_DLX) // Si falla después de reintento, va al DLX
                .build();
    }

    @Bean
    Binding invoicePaymentsBinding() {
        return BindingBuilder.bind(invoicePaymentsQueue()).to(invoiceExchange());
    }

    @Bean
    Binding invoicePaymentsDlqBinding() {
        return BindingBuilder.bind(invoicePaymentsDlq()).to(invoicePaymentsDlx());
    }

    @Bean
    Binding invoicePaymentsRetryBinding() {
        return BindingBuilder.bind(invoicePaymentsRetryQueue()).to(invoicePaymentsDlx());
    }

    // Account Queues and Bindings 
    @Bean
    Queue accountPaymentsQueue() {
        return QueueBuilder.durable(ACCOUNT_PAYMENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_PAYMENTS_DLX)
                .build();
    }

    @Bean
    Queue accountPaymentsDlq() {
        return QueueBuilder.durable(ACCOUNT_PAYMENTS_DLQ).build();
    }

    @Bean
    Queue accountPaymentsRetryQueue() {
        return QueueBuilder.durable(ACCOUNT_PAYMENTS_RETRY_QUEUE)
                .withArgument("x-message-ttl", 60000)
                .withArgument("x-dead-letter-exchange", ACCOUNT_PAYMENTS_DLX)
                .build();
    }

    @Bean
    Binding accountPaymentsBinding() {
        return BindingBuilder.bind(accountPaymentsQueue()).to(accountExchange());
    }

    @Bean
    Binding accountPaymentsDlqBinding() {
        return BindingBuilder.bind(accountPaymentsDlq()).to(accountPaymentsDlx());
    }

    @Bean
    Binding accountPaymentsRetryBinding() {
        return BindingBuilder.bind(accountPaymentsRetryQueue()).to(accountPaymentsDlx());
    }

    // Method payments Queues and Bindings
    @Bean
    Queue methodPaymentPaymentsQueue() {
        return QueueBuilder.durable(METHOD_PAYMENT_PAYMENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", METHOD_PAYMENT_PAYMENTS_DLX)
                .build();
    }

    @Bean
    Queue methodPaymentPaymentsDlq() {
        return QueueBuilder.durable(METHOD_PAYMENT_PAYMENTS_DLQ).build();
    }

    @Bean
    Queue methodPaymentPaymentsRetryQueue() {
        return QueueBuilder.durable(METHOD_PAYMENT_PAYMENTS_RETRY_QUEUE)
                .withArgument("x-message-ttl", 60000)
                .withArgument("x-dead-letter-exchange", METHOD_PAYMENT_PAYMENTS_DLX)
                .build();
    }

    @Bean
    Binding methodPaymentPaymentsBinding() {
        return BindingBuilder.bind(methodPaymentPaymentsQueue()).to(methodPaymentExchange());
    }

    @Bean
    Binding methodPaymentPaymentsDlqBinding() {
        return BindingBuilder.bind(methodPaymentPaymentsDlq()).to(methodPaymentPaymentsDlx());
    }

    @Bean
    Binding methodPaymentPaymentsRetryBinding() {
        return BindingBuilder.bind(methodPaymentPaymentsRetryQueue()).to(methodPaymentPaymentsDlx());
    }


    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setMandatory(true);
        
        template.setReturnsCallback(returned -> {
            log.error("Message returned: {}", returned.getMessage());
        });

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message not delivered to exchange. Cause: {}", cause);
            }
        });
        return template;
    }

    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
