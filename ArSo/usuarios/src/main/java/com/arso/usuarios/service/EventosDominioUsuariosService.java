package com.arso.usuarios.service;

import com.arso.usuarios.event.CompraventaCreadaEvent;
import com.arso.usuarios.port.incoming.EventosDominioPort;

public class EventosDominioUsuariosService implements EventosDominioPort {

    private final ServicioUsuarios servicioUsuarios;

    public EventosDominioUsuariosService(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @Override
    public void procesarEventoCompraventaCreada(CompraventaCreadaEvent event) {
        servicioUsuarios.registrarCompraventa(event.getIdComprador(), event.getIdVendedor());
    }

    @Override
    public void procesarEventoValoracionCreada(com.arso.usuarios.event.ValoracionCreadaEvent event) {
        servicioUsuarios.registrarValoracion(event.getIdUsuarioValorado(), event.getRolUsuarioValorado(), event.getPuntuacion());
    }
}
