package com.arso.productos.adapter.rabbitmq;

import com.arso.productos.event.ProductoCreadoEvent;
import com.arso.productos.event.ProductoModificadoEvent;
import com.arso.productos.port.EventPublisherPort;
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
    private final String productoCreadoRoutingKey;
    private final String productoModificadoRoutingKey;

    public RabbitMqEventPublisherAdapter(RabbitTemplate rabbitTemplate,
                                         @Value("${app.rabbitmq.exchange}") String exchange,
                                         @Value("${app.rabbitmq.routing-key.producto-creado}") String productoCreadoRoutingKey,
                                         @Value("${app.rabbitmq.routing-key.producto-modificado}") String productoModificadoRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.productoCreadoRoutingKey = productoCreadoRoutingKey;
        this.productoModificadoRoutingKey = productoModificadoRoutingKey;
    }

    @Override
    public void publicarProductoCreado(ProductoCreadoEvent event) {
        publicar(productoCreadoRoutingKey, event);
    }

    @Override
    public void publicarProductoModificado(ProductoModificadoEvent event) {
        publicar(productoModificadoRoutingKey, event);
    }

    private void publicar(String routingKey, Object event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (RuntimeException ex) {
            log.warn("No se pudo publicar el evento {} en RabbitMQ", routingKey, ex);
        }
    }
}
