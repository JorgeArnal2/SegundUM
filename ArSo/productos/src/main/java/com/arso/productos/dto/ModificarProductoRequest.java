package com.arso.productos.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

public class ModificarProductoRequest {

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    private Double precio;

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
}

