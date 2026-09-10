package com.arso.usuarios.bus;

import com.arso.usuarios.event.CompraventaCreadaEvent;
import com.arso.usuarios.port.incoming.EventosDominioPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class UsuarioCompraventaConsumer implements AutoCloseable {

    private static final String COMPRAVENTA_CREADA_ROUTING_KEY = "bus.compraventas.compraventa-creada";

    private static final Logger log = LoggerFactory.getLogger(UsuarioCompraventaConsumer.class);

    private final EventosDominioPort eventosDominioPort;
    private final RabbitMqProperties properties;
    private final ConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private Channel channel;

    public UsuarioCompraventaConsumer(EventosDominioPort eventosDominioPort) {
        this.eventosDominioPort = eventosDominioPort;
        this.properties = new RabbitMqProperties();
        this.connectionFactory = RabbitMqConnectionFactoryProvider.create(properties);
        this.objectMapper = new ObjectMapper();
    }

    public void start() {
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(properties.getExchange(), "topic", true);
            channel.queueDeclare(properties.getMicroserviceQueue(), true, false, false, null);
            channel.queueBind(properties.getMicroserviceQueue(), properties.getExchange(), properties.getCompraventasBindingPattern());

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String payload = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String routingKey = delivery.getEnvelope().getRoutingKey();
                try {
                    if (!COMPRAVENTA_CREADA_ROUTING_KEY.equals(routingKey)) {
                        log.debug("Evento ignorado por usuarios para routing key {}", routingKey);
                        return;
                    }

                    CompraventaCreadaEvent event = objectMapper.readValue(payload, CompraventaCreadaEvent.class);
                    eventosDominioPort.procesarEventoCompraventaCreada(event);
                } catch (IOException | RuntimeException e) {
                    log.warn("No se pudo procesar el evento {} recibido en usuarios", routingKey, e);
                }
            };
            CancelCallback cancelCallback = consumerTag -> { };
            channel.basicConsume(properties.getMicroserviceQueue(), true, deliverCallback, cancelCallback);
        } catch (IOException | TimeoutException e) {
            log.warn("No se pudo arrancar el consumidor RabbitMQ de usuarios", e);
        }
    }

    @Override
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            log.warn("No se pudo cerrar el consumidor RabbitMQ de usuarios", e);
        }
    }
}
