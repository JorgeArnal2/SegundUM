package com.arso.compraventas.dto;

import javax.validation.constraints.NotBlank;

public class CrearCompraventaRequest {

    @NotBlank(message = "El identificador del producto es obligatorio")
    private String idProducto;

    @NotBlank(message = "El identificador del comprador es obligatorio")
    private String idComprador;

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getIdComprador() { return idComprador; }
    public void setIdComprador(String idComprador) { this.idComprador = idComprador; }
}
