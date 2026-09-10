package com.arso.compraventas.adapter.rabbitmq;

import com.arso.compraventas.event.CompraventaCreadaEvent;
import com.arso.compraventas.port.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqEventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqEventPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String compraventaCreadaRoutingKey;

    public RabbitMqEventPublisherAdapter(RabbitTemplate rabbitTemplate,
                                         @Value("${app.rabbitmq.exchange}") String exchange,
                                         @Value("${app.rabbitmq.routing-key.compraventa-creada}") String compraventaCreadaRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.compraventaCreadaRoutingKey = compraventaCreadaRoutingKey;
    }

    @Override
    public void publicarCompraventaCreada(CompraventaCreadaEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, compraventaCreadaRoutingKey, event);
        } catch (RuntimeException ex) {
            log.warn("No se pudo publicar el evento de compraventa {} en RabbitMQ", event.getIdCompraventa(), ex);
        }
    }
}
