package com.arso.compraventas.service;

import com.arso.compraventas.domain.Compraventa;
import com.arso.compraventas.event.CompraventaCreadaEvent;
import com.arso.compraventas.exception.ErrorNegocioException;
import com.arso.compraventas.exception.RecursoNoEncontradoException;
import com.arso.compraventas.port.EventPublisherPort;
import com.arso.compraventas.port.ProductosPort;
import com.arso.compraventas.port.UsuariosPort;
import com.arso.compraventas.port.dto.ProductoRemotoDto;
import com.arso.compraventas.port.dto.UsuarioRemotoDto;
import com.arso.compraventas.repository.CompraventaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ServicioCompraventasImpl implements ServicioCompraventas {

    private final CompraventaRepository repository;
    private final EventPublisherPort eventPublisherPort;
    private final ProductosPort productosPort;
    private final UsuariosPort usuariosPort;

    public ServicioCompraventasImpl(CompraventaRepository repository,
                                    EventPublisherPort eventPublisherPort,
                                    ProductosPort productosPort,
                                    UsuariosPort usuariosPort) {
        this.repository = repository;
        this.eventPublisherPort = eventPublisherPort;
        this.productosPort = productosPort;
        this.usuariosPort = usuariosPort;
    }

    @Override
    public Compraventa crearCompraventa(String idProducto, String idComprador) {
        // Recuperar información del producto
        ProductoRemotoDto producto = productosPort.getProducto(idProducto);

        if (producto.isVendido()) {
            throw new ErrorNegocioException("El producto ya ha sido vendido");
        }

        if (repository.existsByIdProducto(idProducto)) {
            throw new ErrorNegocioException("El producto ya ha sido comprado");
        }

        String idVendedor = producto.getVendedor() != null
                ? producto.getVendedor().getId()
                : null;

        // Recuperar nombres de comprador y vendedor
        UsuarioRemotoDto comprador = usuariosPort.getNombreUsuario(idComprador);
        UsuarioRemotoDto vendedor = idVendedor != null
                ? usuariosPort.getNombreUsuario(idVendedor)
                : null;

        // Construir texto de recogida
        String recogidaTexto = "";
        if (producto.getRecogida() != null) {
            recogidaTexto = producto.getRecogida().toTexto();
        }

        // Crear entidad
        Compraventa compraventa = new Compraventa();
        compraventa.setId(UUID.randomUUID().toString());
        compraventa.setIdProducto(idProducto);
        compraventa.setTitulo(producto.getTitulo());
        compraventa.setPrecio(producto.getPrecio());
        compraventa.setRecogida(recogidaTexto);
        compraventa.setIdVendedor(idVendedor);
        compraventa.setNombreVendedor(vendedor != null ? vendedor.getNombreCompleto() : "");
        compraventa.setIdComprador(idComprador);
        compraventa.setNombreComprador(comprador.getNombreCompleto());
        compraventa.setFecha(Instant.now());

        Compraventa savedCompraventa = repository.save(compraventa);
        eventPublisherPort.publicarCompraventaCreada(CompraventaCreadaEvent.from(savedCompraventa));
        return savedCompraventa;
    }

    @Override
    public Compraventa getCompraventa(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compraventa no encontrada con id: " + id));
    }

    @Override
    public Page<Compraventa> getComprasPorComprador(String idComprador, Pageable pageable) {
        return repository.findByIdComprador(idComprador, pageable);
    }

    @Override
    public Page<Compraventa> getVentasPorVendedor(String idVendedor, Pageable pageable) {
        return repository.findByIdVendedor(idVendedor, pageable);
    }

    @Override
    public Page<Compraventa> getCompraventasPorCompradorYVendedor(String idComprador,
                                                                   String idVendedor,
                                                                   Pageable pageable) {
        return repository.findByIdCompradorAndIdVendedor(idComprador, idVendedor, pageable);
    }
}
