package com.arso.productos.service;

import com.arso.productos.dto.CategoriaDto;

import java.util.List;

public interface ServicioCategorias {
    List<CategoriaDto> getCategoriasRaiz();
    CategoriaDto.CategoriaDetalleDto getCategoriaPorId(String id);
}
