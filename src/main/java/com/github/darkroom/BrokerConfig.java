package com.github.darkroom;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.UUID;

@Configuration
public class BrokerConfig {

    public static final String RESULTS_EXCHANGE = "results";
    public static final String TASKS_EXCHANGE = "tasks";

    @Value("#{systemProperties['RABBIT_URI'] ?: 'amqp://localhost:5672'}")
    private String rabbitUri;

    @Value("#{systemProperties['RABBIT_USERNAME'] ?: 'rabbitmq'}")
    private String rabbitUsername;

    @Value("#{systemProperties['RABBIT_PASSWORD'] ?: 'rabbitmq'}")
    private String rabbitPassword;

    @Bean
    public Queue queue() {
        var queueName = UUID.randomUUID().toString();
        var arguments = new HashMap<String, Object>();
        return new Queue(queueName, false, true, true, arguments);
    }

    @Bean
    public DirectExchange resultsExchange() {
        return new DirectExchange(RESULTS_EXCHANGE, false, true);
    }

    @Bean
    public DirectExchange tasksExchange() {
        return new DirectExchange(TASKS_EXCHANGE, false, true);
    }

    @Bean
    public Binding binding(Queue queue, @Qualifier("resultsExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        var factory = new CachingConnectionFactory();
        factory.setUri(rabbitUri);
        factory.setUsername(rabbitUsername);
        factory.setPassword(rabbitPassword);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        final var rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
