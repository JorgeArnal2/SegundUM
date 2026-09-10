package com.arso.productos.dto;

import com.arso.productos.domain.EstadoProducto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CrearProductoRequest {

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    private Double precio;

    @NotNull(message = "El estado es obligatorio")
    private EstadoProducto estado;

    @NotBlank(message = "idCategoria es obligatorio")
    private String idCategoria;

    @NotNull(message = "envioDisponible es obligatorio")
    private Boolean envioDisponible;

    @NotBlank(message = "idVendedor es obligatorio")
    private String idVendedor;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Boolean getEnvioDisponible() {
        return envioDisponible;
    }

    public void setEnvioDisponible(Boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }
}

