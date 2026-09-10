package com.arso.productos.port;

import com.arso.productos.event.ProductoCreadoEvent;
import com.arso.productos.event.ProductoModificadoEvent;

public interface EventPublisherPort {

    void publicarProductoCreado(ProductoCreadoEvent event);

    void publicarProductoModificado(ProductoModificadoEvent event);
}
