package com.arso.compraventas.adapter.retrofit;

import com.arso.compraventas.exception.RecursoNoEncontradoException;
import com.arso.compraventas.port.UsuariosPort;
import com.arso.compraventas.port.dto.UsuarioRemotoDto;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;

@Component
public class UsuariosAdapter implements UsuariosPort {

    private final UsuariosApiClient client;

    public UsuariosAdapter(UsuariosApiClient client) {
        this.client = client;
    }

    @Override
    public UsuarioRemotoDto getNombreUsuario(String idUsuario) {
        try {
            Response<UsuarioRemotoDto> response = client.getNombreUsuario(idUsuario).execute();
            if (response.code() == 404) {
                throw new RecursoNoEncontradoException("Usuario no encontrado: " + idUsuario);
            }
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Error al obtener el usuario " + idUsuario + ": HTTP " + response.code());
            }
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException("Error de comunicación con el microservicio Usuarios", e);
        }
    }
}
