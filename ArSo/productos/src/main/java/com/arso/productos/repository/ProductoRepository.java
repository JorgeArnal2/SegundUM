package com.arso.productos.repository;

import com.arso.productos.domain.EstadoProducto;
import com.arso.productos.domain.Producto;
import com.arso.repository.Repository;

import java.util.List;

public interface ProductoRepository extends Repository<Producto, String> {
    Producto findById(String id);
    List<Producto> findAll();
    Producto save(Producto producto);
    void deleteProducto(Producto producto);
    List<Producto> findBy(String rutaCategoria, String description, EstadoProducto estado, Double maxPrice);
    List<Producto> findHistorialMes(int mes, int anio);
    List<Producto> findByVendedor(String idVendedor);
}
