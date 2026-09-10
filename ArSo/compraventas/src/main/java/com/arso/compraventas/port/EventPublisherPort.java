package com.arso.compraventas.port;

import com.arso.compraventas.event.CompraventaCreadaEvent;

public interface EventPublisherPort {

    void publicarCompraventaCreada(CompraventaCreadaEvent event);
}
