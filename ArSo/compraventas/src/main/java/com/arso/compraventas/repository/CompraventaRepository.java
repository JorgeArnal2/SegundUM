package com.arso.compraventas.repository;

import com.arso.compraventas.domain.Compraventa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompraventaRepository extends MongoRepository<Compraventa, String> {

    Page<Compraventa> findByIdComprador(String idComprador, Pageable pageable);

    Page<Compraventa> findByIdVendedor(String idVendedor, Pageable pageable);

    Page<Compraventa> findByIdCompradorAndIdVendedor(String idComprador, String idVendedor, Pageable pageable);

    boolean existsByIdProducto(String idProducto);
}
