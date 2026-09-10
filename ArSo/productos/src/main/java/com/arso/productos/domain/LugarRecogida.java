package com.arso.productos.domain;

import javax.persistence.Embeddable;
import javax.persistence.Column;
import javax.persistence.Table;


@Embeddable
@Table(name = "lugar_recogida")
public class LugarRecogida {

    @Column (name="descripcion", nullable=false, length=500 )
    private String descripcion;
    @Column (name="longitud" )
    private Double longitud;
    @Column (name="latitud" )
    private Double latitud;

    // Getters and setters

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
