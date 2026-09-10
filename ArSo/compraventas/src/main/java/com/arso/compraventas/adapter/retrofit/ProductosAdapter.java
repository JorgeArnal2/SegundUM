package com.arso.compraventas.adapter.retrofit;

import com.arso.compraventas.exception.RecursoNoEncontradoException;
import com.arso.compraventas.port.ProductosPort;
import com.arso.compraventas.port.dto.ProductoRemotoDto;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;

@Component
public class ProductosAdapter implements ProductosPort {

    private final ProductosApiClient client;

    public ProductosAdapter(ProductosApiClient client) {
        this.client = client;
    }

    @Override
    public ProductoRemotoDto getProducto(String idProducto) {
        try {
            Response<ProductoRemotoDto> response = client.getProducto(idProducto).execute();
            if (response.code() == 404) {
                throw new RecursoNoEncontradoException("Producto no encontrado: " + idProducto);
            }
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Error al obtener el producto " + idProducto + ": HTTP " + response.code());
            }
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException("Error de comunicación con el microservicio Productos", e);
        }
    }
}
