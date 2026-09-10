package com.arso.productos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    @Bean
    public TopicExchange busExchange(@Value("${app.rabbitmq.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue productosQueue(@Value("${app.rabbitmq.queue.microservice}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding usuariosBinding(@Qualifier("productosQueue") Queue productosQueue,
                                        TopicExchange busExchange,
                                        @Value("${app.rabbitmq.binding-pattern.usuarios}") String bindingPattern) {
        return BindingBuilder.bind(productosQueue).to(busExchange).with(bindingPattern);
    }

    @Bean
    public Binding compraventasBinding(@Qualifier("productosQueue") Queue productosQueue,
                                            TopicExchange busExchange,
                                            @Value("${app.rabbitmq.binding-pattern.compraventas}") String bindingPattern) {
        return BindingBuilder.bind(productosQueue).to(busExchange).with(bindingPattern);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
