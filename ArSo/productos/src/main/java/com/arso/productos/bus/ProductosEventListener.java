package com.arso.productos.bus;

import com.arso.productos.event.CompraventaCreadaEventPayload;
import com.arso.productos.event.UsuarioEventPayload;
import com.arso.productos.port.incoming.EventosDominioPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProductosEventListener {

    private static final String USUARIO_CREADO = "bus.usuarios.usuario-creado";
    private static final String USUARIO_MODIFICADO = "bus.usuarios.usuario-modificado";
    private static final String COMPRAVENTA_CREADA = "bus.compraventas.compraventa-creada";

    private static final Logger log = LoggerFactory.getLogger(ProductosEventListener.class);

    private final EventosDominioPort eventosDominioPort;
    private final ObjectMapper objectMapper;

    public ProductosEventListener(EventosDominioPort eventosDominioPort, ObjectMapper objectMapper) {
        this.eventosDominioPort = eventosDominioPort;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.microservice}")
    public void onDomainEvent(Message message, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        try {
            if (USUARIO_CREADO.equals(routingKey) || USUARIO_MODIFICADO.equals(routingKey)) {
                UsuarioEventPayload event = objectMapper.readValue(message.getBody(), UsuarioEventPayload.class);
                eventosDominioPort.procesarEventoUsuario(event);
                return;
            }

            if (COMPRAVENTA_CREADA.equals(routingKey)) {
                CompraventaCreadaEventPayload event = objectMapper.readValue(message.getBody(), CompraventaCreadaEventPayload.class);
                eventosDominioPort.procesarEventoCompraventaCreada(event);
                return;
            }

            log.debug("Evento ignorado por productos para routing key {}", routingKey);
        } catch (IOException | RuntimeException e) {
            log.warn("No se pudo procesar el evento {} en productos", routingKey, e);
        }
    }
}
