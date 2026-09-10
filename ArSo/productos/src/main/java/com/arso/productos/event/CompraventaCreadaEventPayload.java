package com.arso.productos.event;

public class CompraventaCreadaEventPayload {

    private String type;
    private String occurredOn;
    private String idCompraventa;
    private String idProducto;
    private String idComprador;
    private String idVendedor;
    private Double precio;
    private String fecha;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(String occurredOn) {
        this.occurredOn = occurredOn;
    }

    public String getIdCompraventa() {
        return idCompraventa;
    }

    public void setIdCompraventa(String idCompraventa) {
        this.idCompraventa = idCompraventa;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
