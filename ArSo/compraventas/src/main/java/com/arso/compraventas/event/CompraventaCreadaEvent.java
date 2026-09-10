package com.arso.compraventas.event;

import com.arso.compraventas.domain.Compraventa;

import java.time.Instant;

public class CompraventaCreadaEvent extends DomainEvent {

    private final String idCompraventa;
    private final String idProducto;
    private final String idComprador;
    private final String idVendedor;
    private final Double precio;
    private final Instant fecha;

    private CompraventaCreadaEvent(String idCompraventa,
                                   String idProducto,
                                   String idComprador,
                                   String idVendedor,
                                   Double precio,
                                   Instant fecha,
                                   Instant occurredOn) {
        super("compraventa-creada", occurredOn);
        this.idCompraventa = idCompraventa;
        this.idProducto = idProducto;
        this.idComprador = idComprador;
        this.idVendedor = idVendedor;
        this.precio = precio;
        this.fecha = fecha;
    }

    public static CompraventaCreadaEvent from(Compraventa compraventa) {
        Instant occurredOn = compraventa.getFecha() != null ? compraventa.getFecha() : Instant.now();
        return new CompraventaCreadaEvent(
                compraventa.getId(),
                compraventa.getIdProducto(),
                compraventa.getIdComprador(),
                compraventa.getIdVendedor(),
                compraventa.getPrecio(),
                compraventa.getFecha(),
                occurredOn
        );
    }

    public String getIdCompraventa() {
        return idCompraventa;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public Double getPrecio() {
        return precio;
    }

    public Instant getFecha() {
        return fecha;
    }
}
