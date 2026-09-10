package com.arso.usuarios.adapter.rabbitmq;

import com.arso.usuarios.bus.RabbitMqConnectionFactoryProvider;
import com.arso.usuarios.bus.RabbitMqProperties;
import com.arso.usuarios.event.UsuarioCreadoEvent;
import com.arso.usuarios.event.UsuarioModificadoEvent;
import com.arso.usuarios.port.EventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RabbitMqEventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqEventPublisherAdapter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RabbitMqProperties properties = new RabbitMqProperties();
    private final ConnectionFactory connectionFactory = RabbitMqConnectionFactoryProvider.create(properties);

    @Override
    public void publicarUsuarioCreado(UsuarioCreadoEvent event) {
        publicar(properties.getUsuarioCreadoRoutingKey(), event);
    }

    @Override
    public void publicarUsuarioModificado(UsuarioModificadoEvent event) {
        publicar(properties.getUsuarioModificadoRoutingKey(), event);
    }

    private void publicar(String routingKey, Object event) {
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(properties.getExchange(), "topic", true);
            byte[] body = objectMapper.writeValueAsString(event).getBytes(StandardCharsets.UTF_8);
            channel.basicPublish(properties.getExchange(), routingKey, null, body);
        } catch (Exception e) {
            log.warn("No se pudo publicar el evento {} en RabbitMQ", routingKey, e);
        }
    }
}
