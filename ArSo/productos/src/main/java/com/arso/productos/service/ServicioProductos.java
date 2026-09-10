package com.arso.productos.service;

import com.arso.productos.domain.EstadoProducto;
import com.arso.productos.domain.Producto;
import com.arso.productos.domain.ProductoResumen;

import java.util.List;

public interface ServicioProductos {
    String altaProducto(String titulo, String descripcion, Double precio, EstadoProducto estado, String idCategoria, boolean envioDisponible, String idVendedor);
    void asignarLugarRecogida(String idProducto, String descripcion, Double longitud, Double latitud);
    void modificarProducto(String idProducto, String descripcion, Double precio);
    void anadirVisualizacion(String idProducto);
    List<ProductoResumen> getHistorialMes(int mes, int anio);
    List<Producto> buscarProductos(String idCategoria, String texto, EstadoProducto estado, Double precioMaximo);
    List<Producto> getProductosPorVendedor(String idVendedor);
    Producto getProductoPorId(String idProducto);
    void guardarUsuarioResumen(String idUsuario, String email, String nombre, String apellidos);
    void marcarProductoComoVendido(String idProducto);
}
