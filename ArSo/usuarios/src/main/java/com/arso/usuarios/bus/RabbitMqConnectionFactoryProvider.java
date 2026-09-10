package com.arso.usuarios.bus;

import com.rabbitmq.client.ConnectionFactory;

public final class RabbitMqConnectionFactoryProvider {

    private RabbitMqConnectionFactoryProvider() {
    }

    public static ConnectionFactory create(RabbitMqProperties properties) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(properties.getHost());
        factory.setPort(properties.getPort());
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());
        return factory;
    }
}
