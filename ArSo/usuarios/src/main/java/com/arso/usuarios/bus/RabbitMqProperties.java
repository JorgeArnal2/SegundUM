package com.arso.usuarios.bus;

import com.arso.utils.PropertiesReader;

import java.io.IOException;

public class RabbitMqProperties {

    private static final String APPLICATION_PROPERTIES = "application.properties";

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String exchange;
    private final String usuarioCreadoRoutingKey;
    private final String usuarioModificadoRoutingKey;
    private final String microserviceQueue;
    private final String compraventasBindingPattern;

    public RabbitMqProperties() {
        try {
            PropertiesReader properties = new PropertiesReader(APPLICATION_PROPERTIES);
            this.host = System.getenv("RABBITMQ_HOST") != null ? System.getenv("RABBITMQ_HOST") : properties.getProperty("rabbitmq.host");
            this.port = Integer.parseInt(properties.getProperty("rabbitmq.port"));
            this.username = properties.getProperty("rabbitmq.username");
            this.password = properties.getProperty("rabbitmq.password");
            this.exchange = properties.getProperty("rabbitmq.exchange");
            this.usuarioCreadoRoutingKey = properties.getProperty("rabbitmq.routing-key.usuario-creado");
            this.usuarioModificadoRoutingKey = properties.getProperty("rabbitmq.routing-key.usuario-modificado");
            this.microserviceQueue = properties.getProperty("rabbitmq.queue.microservice");
            this.compraventasBindingPattern = properties.getProperty("rabbitmq.binding-pattern.compraventas");
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la configuracion de RabbitMQ", e);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getExchange() {
        return exchange;
    }

    public String getUsuarioCreadoRoutingKey() {
        return usuarioCreadoRoutingKey;
    }

    public String getUsuarioModificadoRoutingKey() {
        return usuarioModificadoRoutingKey;
    }

    public String getMicroserviceQueue() {
        return microserviceQueue;
    }

    public String getCompraventasBindingPattern() {
        return compraventasBindingPattern;
    }
}
