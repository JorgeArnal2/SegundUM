package com.arso.productos.port.incoming;

import com.arso.productos.event.CompraventaCreadaEventPayload;
import com.arso.productos.event.UsuarioEventPayload;

public interface EventosDominioPort {

    void procesarEventoUsuario(UsuarioEventPayload event);

    void procesarEventoCompraventaCreada(CompraventaCreadaEventPayload event);
}
