package com.arso.productos.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LugarRecogidaRequest {

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
}

