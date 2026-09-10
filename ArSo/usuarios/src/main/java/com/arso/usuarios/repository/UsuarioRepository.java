package com.arso.usuarios.repository;

import com.arso.usuarios.domain.Usuario;

import java.util.List;

public interface UsuarioRepository {
    Usuario findById(String id);
    Usuario findByEmailAndClave(String email, String clave);
    List<Usuario> findAll();
    Usuario save(Usuario usuario);
    void delete(Usuario usuario);
    boolean existeEmail(String email);
    Usuario obtenerPorEmail(String email);
    Usuario obtenerPorGithubId(String githubId);
}
