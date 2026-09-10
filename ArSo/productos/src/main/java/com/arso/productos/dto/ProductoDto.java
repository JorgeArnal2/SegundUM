package com.arso.productos.dto;


import com.arso.productos.domain.Categoria;
import com.arso.productos.domain.LugarRecogida;
import com.arso.productos.domain.Producto;
import com.arso.productos.domain.UsuarioResumen;

import java.time.Instant;

public class ProductoDto {
    private String id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private String estado;
    private String fechaPublicacion;
    private Integer visualizaciones;
    private boolean envioDisponible;
    private boolean vendido;
    private CategoriaDto categoria;
    private UsuarioResumenDto vendedor;
    private LugarRecogidaDto recogida;

    public UsuarioResumenDto getVendedor() {
        return vendedor;
    }

    public void setVendedor(UsuarioResumenDto vendedor) {
        this.vendedor = vendedor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public CategoriaDto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDto categoria) {
        this.categoria = categoria;
    }

    public Integer getVisualizaciones() {
        return visualizaciones;
    }

    public void setVisualizaciones(Integer visualizaciones) {
        this.visualizaciones = visualizaciones;
    }

    public boolean isEnvioDisponible() {
        return envioDisponible;
    }

    public void setEnvioDisponible(boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }

    public boolean isVendido() {
        return vendido;
    }

    public void setVendido(boolean vendido) {
        this.vendido = vendido;
    }

    public LugarRecogidaDto getRecogida() {
        return recogida;
    }

    public void setRecogida(LugarRecogidaDto recogida) {
        this.recogida = recogida;
    }

    public static ProductoDto fromEntity(Producto producto) {
        ProductoDto dto = new ProductoDto();
        dto.setId(producto.getId());
        dto.setTitulo(producto.getTitulo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setEstado(producto.getEstado().toString());
        dto.setFechaPublicacion(Instant.ofEpochMilli(producto.getFechaPublicacion().getTime()).toString());
        dto.setCategoria(CategoriaDto.fromEntity(producto.getCategoria()));
        dto.setVendedor(UsuarioResumenDto.fromEntity(producto.getVendedor()));
        dto.setVisualizaciones(producto.getVisualizaciones());
        dto.setEnvioDisponible(producto.isEnvioDisponible());
        dto.setVendido(producto.isVendido());
        dto.setRecogida(LugarRecogidaDto.fromEntity(producto.getRecogida()));
        return dto;
    }

    public static class CategoriaDto {
        private String id;
        private String nombre;

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

        public static CategoriaDto fromEntity(Categoria categoria) {
            if (categoria == null) {
                return null;
            }
            CategoriaDto dto = new CategoriaDto();
            dto.setId(categoria.getId());
            dto.setNombre(categoria.getNombre());
            return dto;
        }
    }

    public static class UsuarioResumenDto {
        private String id;
        private String nombre;
        private String apellidos;
        private String email;

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

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public static UsuarioResumenDto fromEntity(UsuarioResumen usuario) {
            if (usuario == null) {
                return null;
            }
            UsuarioResumenDto dto = new UsuarioResumenDto();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setApellidos(usuario.getApellidos());
            dto.setEmail(usuario.getEmail());
            return dto;
        }
    }

    public static class LugarRecogidaDto {
        private String descripcion;
        private Double longitud;
        private Double latitud;

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Double getLongitud() {
            return longitud;
        }

        public void setLongitud(Double longitud) {
            this.longitud = longitud;
        }

        public Double getLatitud() {
            return latitud;
        }

        public void setLatitud(Double latitud) {
            this.latitud = latitud;
        }

        public static LugarRecogidaDto fromEntity(LugarRecogida lugar) {
            if (lugar == null) {
                return null;
            }
            LugarRecogidaDto dto = new LugarRecogidaDto();
            dto.setDescripcion(lugar.getDescripcion());
            dto.setLongitud(lugar.getLongitud());
            dto.setLatitud(lugar.getLatitud());
            return dto;
        }
    }
}
