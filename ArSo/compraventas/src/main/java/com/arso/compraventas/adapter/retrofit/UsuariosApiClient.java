package com.arso.compraventas.adapter.retrofit;

import com.arso.compraventas.port.dto.UsuarioRemotoDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UsuariosApiClient {

    @GET("usuarios/{id}/nombre")
    Call<UsuarioRemotoDto> getNombreUsuario(@Path("id") String id);
}
