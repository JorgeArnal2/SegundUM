package com.arso.productos.event;

import com.arso.productos.domain.Producto;
import com.arso.productos.domain.UsuarioResumen;

import java.time.Instant;

public class ProductoModificadoEvent extends DomainEvent {

    private final String idProducto;
    private final String titulo;
    private final String descripcion;
    private final Double precio;
    private final String estado;
    private final boolean envioDisponible;
    private final boolean vendido;
    private final String idVendedor;

    private ProductoModificadoEvent(String idProducto,
                                    String titulo,
                                    String descripcion,
                                    Double precio,
                                    String estado,
                                    boolean envioDisponible,
                                    boolean vendido,
                                    String idVendedor,
                                    String occurredOn) {
        super("producto-modificado", occurredOn);
        this.idProducto = idProducto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.envioDisponible = envioDisponible;
        this.vendido = vendido;
        this.idVendedor = idVendedor;
    }

    public static ProductoModificadoEvent from(Producto producto) {
        UsuarioResumen vendedor = producto.getVendedor();
        String estado = producto.getEstado() != null ? producto.getEstado().toString() : null;
        return new ProductoModificadoEvent(
            producto.getId(),
            producto.getTitulo(),
            producto.getDescripcion(),
            producto.getPrecio(),
            estado,
            producto.isEnvioDisponible(),
            producto.isVendido(),
            vendedor != null ? vendedor.getId() : null,
            Instant.now().toString()
        );
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isEnvioDisponible() {
        return envioDisponible;
    }

    public boolean isVendido() {
        return vendido;
    }

    public String getIdVendedor() {
        return idVendedor;
    }
}
