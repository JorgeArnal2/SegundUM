package com.arso.productos.dto;

import com.arso.productos.domain.Categoria;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaDto {
    private String id;
    private String nombre;
    private String descripcion;
    private String ruta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public static CategoriaDto fromEntity(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        CategoriaDto dto = new CategoriaDto();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setRuta(categoria.getRuta());
        return dto;
    }

    public static class CategoriaDetalleDto extends CategoriaDto {
        private List<CategoriaDto> subcategorias;

        public List<CategoriaDto> getSubcategorias() {
            return subcategorias;
        }

        public void setSubcategorias(List<CategoriaDto> subcategorias) {
            this.subcategorias = subcategorias;
        }

        public static CategoriaDetalleDto fromEntity(Categoria categoria) {
            if (categoria == null) {
                return null;
            }
            CategoriaDetalleDto dto = new CategoriaDetalleDto();
            dto.setId(categoria.getId());
            dto.setNombre(categoria.getNombre());
            dto.setDescripcion(categoria.getDescripcion());
            dto.setRuta(categoria.getRuta());
            dto.setSubcategorias(categoria.getSubcategorias().stream()
                .map(CategoriaDto::fromEntity)
                .collect(Collectors.toList()));
            return dto;
        }
    }
}
