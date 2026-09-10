package com.arso.usuarios.port.incoming;

import com.arso.usuarios.event.CompraventaCreadaEvent;

public interface EventosDominioPort {

    void procesarEventoCompraventaCreada(CompraventaCreadaEvent event);
    void procesarEventoValoracionCreada(com.arso.usuarios.event.ValoracionCreadaEvent event);
}
