package com.arso.compraventas.service;

import com.arso.compraventas.domain.Compraventa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicioCompraventas {

    /**
     * Registra la compraventa de un producto.
     * Recupera la información del producto de Productos y los nombres de Usuarios.
     */
    Compraventa crearCompraventa(String idProducto, String idComprador);

    /** Devuelve una compraventa por su identificador. */
    Compraventa getCompraventa(String id);

    /** Devuelve las compras realizadas por un comprador (paginado). */
    Page<Compraventa> getComprasPorComprador(String idComprador, Pageable pageable);

    /** Devuelve las ventas realizadas por un vendedor (paginado). */
    Page<Compraventa> getVentasPorVendedor(String idVendedor, Pageable pageable);

    /** Devuelve las compraventas entre un comprador y un vendedor concretos (paginado). */
    Page<Compraventa> getCompraventasPorCompradorYVendedor(String idComprador, String idVendedor, Pageable pageable);
}
