package com.arso.productos.service;

import com.arso.productos.dto.CategoriaDto;
import com.arso.productos.exception.RecursoNoEncontradoException;
import com.arso.productos.repository.CategoriaRepositoryJPA;
import com.arso.repository.EntityNotFound;
import com.arso.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioCategoriasImpl implements ServicioCategorias {

    @Autowired
    private CategoriaRepositoryJPA categoriaRepo;

    @Override
    public List<CategoriaDto> getCategoriasRaiz() {
        return categoriaRepo.findRootCategories().stream()
            .map(CategoriaDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDto.CategoriaDetalleDto getCategoriaPorId(String id) {
        try {
            return CategoriaDto.CategoriaDetalleDto.fromEntity(categoriaRepo.getById(id));
        } catch (EntityNotFound ex) {
            throw new RecursoNoEncontradoException("Categoria no encontrada: " + id);
        } catch (RepositoryException ex) {
            throw new RecursoNoEncontradoException("Categoria no encontrada: " + id);
        }
    }
}
