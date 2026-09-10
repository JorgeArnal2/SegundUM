package com.arso.usuarios.bus;

import com.arso.usuarios.event.ValoracionCreadaEvent;
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

public class UsuarioValoracionConsumer implements AutoCloseable {

    private static final String VALORACION_CREADA_ROUTING_KEY = "bus.valoraciones.valoracion-creada";

    private static final Logger log = LoggerFactory.getLogger(UsuarioValoracionConsumer.class);

    private final EventosDominioPort eventosDominioPort;
    private final RabbitMqProperties properties;
    private final ConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private Channel channel;

    public UsuarioValoracionConsumer(EventosDominioPort eventosDominioPort) {
        this.eventosDominioPort = eventosDominioPort;
        this.properties = new RabbitMqProperties();
        this.connectionFactory = RabbitMqConnectionFactoryProvider.create(properties);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void start() {
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(properties.getExchange(), "topic", true);
            
            // Reutilizar la cola del microservicio o usar una propia (usamos la del microservicio)
            String queueName = properties.getMicroserviceQueue() + "-valoraciones";
            channel.queueDeclare(queueName, true, false, false, null);
            
            // Bind específico para este evento
            channel.queueBind(queueName, properties.getExchange(), VALORACION_CREADA_ROUTING_KEY);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String payload = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String routingKey = delivery.getEnvelope().getRoutingKey();
                try {
                    if (!VALORACION_CREADA_ROUTING_KEY.equals(routingKey)) {
                        return; // Lo ignora, será procesado por otro consumer si corresponde
                    }

                    log.info("RECIBIDO EVENTO EN USUARIOS: " + payload);
                    ValoracionCreadaEvent event = objectMapper.readValue(payload, ValoracionCreadaEvent.class);
                    log.info("PARSEADO EVENTO: Valorado=" + event.getIdUsuarioValorado() + ", Rol=" + event.getRolUsuarioValorado() + ", Puntuacion=" + event.getPuntuacion());
                    eventosDominioPort.procesarEventoValoracionCreada(event);
                } catch (IOException | RuntimeException e) {
                    log.warn("No se pudo procesar el evento {} recibido en usuarios", routingKey, e);
                }
            };
            CancelCallback cancelCallback = consumerTag -> { };
            channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
        } catch (IOException | TimeoutException e) {
            log.warn("No se pudo arrancar el consumidor de valoraciones de usuarios", e);
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
            log.warn("No se pudo cerrar el consumidor de valoraciones de usuarios", e);
        }
    }
}
