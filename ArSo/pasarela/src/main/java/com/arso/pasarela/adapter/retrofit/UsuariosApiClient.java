package com.arso.pasarela.adapter.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuariosApiClient {

    @POST("usuarios/credenciales")
    Call<UsuarioAuthDTO> verificarCredenciales(@Body CredencialesDTO credenciales);

    @POST("usuarios/github")
    Call<UsuarioAuthDTO> loginGithub(@Body GithubLoginRequestDTO request);
}
