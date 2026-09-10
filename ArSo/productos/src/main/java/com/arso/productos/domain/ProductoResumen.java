package com.arso.productos.domain;

import java.util.Date;

/**
 * DTO con información resumida de un producto.
 * Se usa para devolver datos esenciales sin exponer toda la entidad.
 */
public class ProductoResumen {

    private String id;
    private String titulo;
    private Double precio;
    private Date fechaAlta;

    public ProductoResumen() {
    }

    public ProductoResumen(String id, String titulo, Double precio, Date fechaAlta) {
        this.id = id;
        this.titulo = titulo;
        this.precio = precio;
        this.fechaAlta = fechaAlta;
    }

    public static ProductoResumen fromProducto(Producto producto) {
        return new ProductoResumen(
                producto.getId(),
                producto.getTitulo(),
                producto.getPrecio(),
                producto.getFechaPublicacion()
        );
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Override
    public String toString() {
        return "ProductoResumen{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", precio=" + precio +
                ", fechaAlta=" + fechaAlta +
                '}';
    }
}
