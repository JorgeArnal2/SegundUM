package com.arso.compraventas.dto;

import com.arso.compraventas.domain.Compraventa;

public class CompraventaDto {

    private String id;
    private String idProducto;
    private String titulo;
    private Double precio;
    private String recogida;
    private String idVendedor;
    private String nombreVendedor;
    private String idComprador;
    private String nombreComprador;
    private String fecha;

    public static CompraventaDto fromEntity(Compraventa c) {
        CompraventaDto dto = new CompraventaDto();
        dto.id = c.getId();
        dto.idProducto = c.getIdProducto();
        dto.titulo = c.getTitulo();
        dto.precio = c.getPrecio();
        dto.recogida = c.getRecogida();
        dto.idVendedor = c.getIdVendedor();
        dto.nombreVendedor = c.getNombreVendedor();
        dto.idComprador = c.getIdComprador();
        dto.nombreComprador = c.getNombreComprador();
        dto.fecha = c.getFecha() != null ? c.getFecha().toString() : null;
        return dto;
    }

    public String getId() { return id; }
    public String getIdProducto() { return idProducto; }
    public String getTitulo() { return titulo; }
    public Double getPrecio() { return precio; }
    public String getRecogida() { return recogida; }
    public String getIdVendedor() { return idVendedor; }
    public String getNombreVendedor() { return nombreVendedor; }
    public String getIdComprador() { return idComprador; }
    public String getNombreComprador() { return nombreComprador; }
    public String getFecha() { return fecha; }
}
