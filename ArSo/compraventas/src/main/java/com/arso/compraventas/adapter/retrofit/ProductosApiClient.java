package com.arso.compraventas.adapter.retrofit;

import com.arso.compraventas.port.dto.ProductoRemotoDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductosApiClient {

    @GET("productos/{id}")
    Call<ProductoRemotoDto> getProducto(@Path("id") String id);
}
