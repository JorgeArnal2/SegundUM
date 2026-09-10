package com.arso.pasarela.auth;

import com.arso.pasarela.adapter.retrofit.CredencialesDTO;
import com.arso.pasarela.adapter.retrofit.GithubLoginRequestDTO;
import com.arso.pasarela.adapter.retrofit.UsuarioAuthDTO;
import com.arso.pasarela.adapter.retrofit.UsuariosApiClient;
import com.arso.pasarela.auth.dto.AuthResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import retrofit2.Response;

import java.io.IOException;

@Service
public class AuthService {

    private final UsuariosApiClient usuariosApiClient;
    private final JwtService jwtService;

    public AuthService(UsuariosApiClient usuariosApiClient, JwtService jwtService) {
        this.usuariosApiClient = usuariosApiClient;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO login(String email, String password) {
        try {
            Response<UsuarioAuthDTO> response = usuariosApiClient
                .verificarCredenciales(new CredencialesDTO(email, password))
                .execute();
            if (response.code() == 401) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
            }
            if (!response.isSuccessful() || response.body() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al validar credenciales");
            }
            return buildAuthResponse(response.body());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Microservicio usuarios no disponible", e);
        }
    }

    public AuthResponseDTO loginWithGithub(String githubId, String email) {
        try {
            Response<UsuarioAuthDTO> response = usuariosApiClient
                .loginGithub(new GithubLoginRequestDTO(githubId, email))
                .execute();
            if (response.code() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }
            if (!response.isSuccessful() || response.body() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al autenticar con GitHub");
            }
            return buildAuthResponse(response.body());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Microservicio usuarios no disponible", e);
        }
    }

    private AuthResponseDTO buildAuthResponse(UsuarioAuthDTO usuario) {
        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setToken(jwtService.generateToken(usuario));
        authResponse.setId(usuario.getId());
        authResponse.setNombreCompleto(usuario.getNombreCompleto());
        authResponse.setRoles(jwtService.resolveResponseRoles(usuario));
        return authResponse;
    }
}
