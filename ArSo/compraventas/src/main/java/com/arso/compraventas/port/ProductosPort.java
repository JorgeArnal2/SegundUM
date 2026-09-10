package com.arso.compraventas.port;

import com.arso.compraventas.port.dto.ProductoRemotoDto;

public interface ProductosPort {
    ProductoRemotoDto getProducto(String idProducto);
}
