package com.arso.productos.service;

import com.arso.productos.domain.*;
import com.arso.productos.event.ProductoCreadoEvent;
import com.arso.productos.event.ProductoModificadoEvent;
import com.arso.productos.exception.ErrorNegocioException;
import com.arso.productos.exception.RecursoNoEncontradoException;
import com.arso.productos.port.EventPublisherPort;
import com.arso.productos.repository.CategoriaRepositoryJPA;
import com.arso.productos.repository.ProductoRepository;
import com.arso.productos.repository.UsuarioResumenRepositoryJPA;
import com.arso.repository.EntityNotFound;
import com.arso.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServicioProductosImpl implements ServicioProductos {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private CategoriaRepositoryJPA categoriaRepo;

    @Autowired
    private UsuarioResumenRepositoryJPA usuarioRepo;

    @Autowired
    private EventPublisherPort eventPublisher;

    @Override
    public String altaProducto(String titulo, String descripcion, Double precio, EstadoProducto estado, String idCategoria, boolean envioDisponible, String idVendedor) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (precio == null || precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        if (idCategoria == null || idCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        if (idVendedor == null || idVendedor.trim().isEmpty()) {
            throw new IllegalArgumentException("El vendedor es obligatorio");
        }

        try {
            Producto producto = new Producto();
            producto.setTitulo(titulo);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setEstado(estado);
            producto.setFechaPublicacion(new Date());
            producto.setVisualizaciones(0);
            producto.setEnvioDisponible(envioDisponible);
            producto.setVendido(false);

            Categoria categoria = categoriaRepo.getById(idCategoria);
            UsuarioResumen vendedor = usuarioRepo.getById(idVendedor);

            producto.setCategoria(categoria);
            producto.setVendedor(vendedor);

            String idProducto = productoRepo.add(producto);
            producto.setId(idProducto);
            eventPublisher.publicarProductoCreado(ProductoCreadoEvent.from(producto));
            return idProducto;
        } catch (EntityNotFound e) {
            throw new RecursoNoEncontradoException("No existe la categoria o vendedor indicado");
        } catch (RepositoryException e) {
            throw new ErrorNegocioException("Error al dar de alta el producto", e);
        }
    }

    @Override
    public void asignarLugarRecogida(String idProducto, String descripcion, Double longitud, Double latitud) {
        try {
            Producto producto = productoRepo.getById(idProducto);
            producto.crearLugarRecogida(descripcion, longitud, latitud);
            productoRepo.update(producto);
            eventPublisher.publicarProductoModificado(ProductoModificadoEvent.from(producto));
        } catch (EntityNotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + idProducto);
        } catch (RepositoryException e) {
            throw new ErrorNegocioException("Error al asignar lugar de recogida", e);
        }
    }

    @Override
    public void modificarProducto(String idProducto, String descripcion, Double precio) {
        try {
            Producto producto = productoRepo.getById(idProducto);
            if (descripcion != null) {
                producto.setDescripcion(descripcion);
            }
            if (precio != null) {
                if (precio <= 0) {
                    throw new IllegalArgumentException("El precio debe ser mayor que 0");
                }
                producto.setPrecio(precio);
            }
            productoRepo.update(producto);
            eventPublisher.publicarProductoModificado(ProductoModificadoEvent.from(producto));
        } catch (EntityNotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + idProducto);
        } catch (RepositoryException e) {
            throw new ErrorNegocioException("Error al modificar el producto", e);
        }
    }

    @Override
    public void anadirVisualizacion(String idProducto) {
        try {
            Producto producto = productoRepo.getById(idProducto);
            producto.setVisualizaciones(producto.getVisualizaciones() + 1);
            productoRepo.update(producto);
        } catch (EntityNotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + idProducto);
        } catch (RepositoryException e) {
            throw new ErrorNegocioException("Error al anadir visualizacion", e);
        }
    }

    @Override
    public List<ProductoResumen> getHistorialMes(int mes, int anio) {
        List<Producto> productos = productoRepo.findHistorialMes(mes, anio);
        return productos.stream()
            .map(ProductoResumen::fromProducto)
            .collect(Collectors.toList());
    }

    @Override
    public List<Producto> buscarProductos(String idCategoria, String texto, EstadoProducto estado, Double precioMaximo) {
        try {
            String rutaCategoria = null;
            if (idCategoria != null && !idCategoria.trim().isEmpty()) {
                Categoria categoria = categoriaRepo.getById(idCategoria);
                rutaCategoria = categoria.getRuta();
            }
            return productoRepo.findBy(rutaCategoria, texto, estado, precioMaximo);
        } catch (EntityNotFound e) {
            throw new RecursoNoEncontradoException("Categoria no encontrada: " + idCategoria);
        } catch (RepositoryException e) {
            throw new ErrorNegocioException("Error al buscar productos", e);
        }
    }

    @Override
    public List<Producto> getProductosPorVendedor(String idVendedor) {
        if (idVendedor == null || idVendedor.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del vendedor es obligatorio");
        }
        try {
            usuarioRepo.getById(idVendedor);
        } catch (EntityNotFound e) {
            throw new RecursoNoEncontradoException("Vendedor no encontrado: " + idVendedor);
        } catch (RepositoryException e) {
            throw new ErrorNegocioException("Error al consultar el vendedor", e);
        }
        return productoRepo.findByVendedor(idVendedor);
    }

    @Override
    public Producto getProductoPorId(String idProducto) {
        try {
            return productoRepo.getById(idProducto);
        } catch (EntityNotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + idProducto);
        } catch (RepositoryException e) {
            throw new ErrorNegocioException("Error al recuperar el producto", e);
        }
    }

    @Override
    public void guardarUsuarioResumen(String idUsuario, String email, String nombre, String apellidos) {
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return;
        }

        UsuarioResumen usuario;
        try {
            usuario = usuarioRepo.getById(idUsuario);
        } catch (EntityNotFound | RepositoryException e) {
            usuario = new UsuarioResumen();
            usuario.setId(idUsuario);
        }

        usuario.setEmail(email);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuarioRepo.save(usuario);
    }

    @Override
    public void marcarProductoComoVendido(String idProducto) {
        if (idProducto == null || idProducto.trim().isEmpty()) {
            return;
        }

        Producto producto = productoRepo.findById(idProducto);
        if (producto == null || producto.isVendido()) {
            return;
        }

        producto.setVendido(true);
        productoRepo.save(producto);
        eventPublisher.publicarProductoModificado(ProductoModificadoEvent.from(producto));
    }
}
