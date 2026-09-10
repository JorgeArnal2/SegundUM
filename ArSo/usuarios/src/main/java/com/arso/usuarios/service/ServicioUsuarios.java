package com.arso.usuarios.service;

import com.arso.usuarios.domain.Usuario;

import java.util.Date;
import java.util.List;

public interface ServicioUsuarios {
    String altaUsuario(String nombre, String apellidos, String email, String clave, Date fechaNacimiento, String telefono);
    void modificarUsuario(String idUsuario, String nombre, String apellidos, String clave, Date fechaNacimiento, String telefono);
    void registrarCompraventa(String idComprador, String idVendedor);
    void registrarValoracion(String idUsuarioValorado, String rolUsuarioValorado, int puntuacion);
    Usuario getUsuario(String idUsuario);
    List<Usuario> getUsuarios();
    Usuario obtenerPorEmail(String email);
    Usuario obtenerPorGithubId(String githubId);
    void actualizarGithubId(String idUsuario, String githubId);
}
