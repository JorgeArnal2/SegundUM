package com.arso.productos.repository;

import com.arso.productos.domain.Categoria;

import java.util.List;

public interface CategoriaRepository {
    Categoria findById(String id);
    List<Categoria> findRootCategories();
    List<Categoria> findAll();
    Categoria save(Categoria categoria);
    void delete(Categoria categoria);
}
