package com.arso.productos.service;

import com.arso.productos.event.CompraventaCreadaEventPayload;
import com.arso.productos.event.UsuarioEventPayload;
import com.arso.productos.port.incoming.EventosDominioPort;
import org.springframework.stereotype.Service;

@Service
public class EventosDominioProductosService implements EventosDominioPort {

    private final ServicioProductos servicioProductos;

    public EventosDominioProductosService(ServicioProductos servicioProductos) {
        this.servicioProductos = servicioProductos;
    }

    @Override
    public void procesarEventoUsuario(UsuarioEventPayload event) {
        servicioProductos.guardarUsuarioResumen(
            event.getIdUsuario(),
            event.getEmail(),
            event.getNombre(),
            event.getApellidos()
        );
    }

    @Override
    public void procesarEventoCompraventaCreada(CompraventaCreadaEventPayload event) {
        servicioProductos.marcarProductoComoVendido(event.getIdProducto());
    }
}
