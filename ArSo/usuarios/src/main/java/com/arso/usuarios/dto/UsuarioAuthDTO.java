package com.arso.usuarios.dto;

import com.arso.usuarios.domain.Usuario;

public class UsuarioAuthDTO {
    private String id;
    private String email;
    private String nombre;
    private String apellidos;
    private boolean administrador;
    private String rol;
    private String githubId;

    public static UsuarioAuthDTO fromUsuario(Usuario usuario) {
        UsuarioAuthDTO dto = new UsuarioAuthDTO();
        dto.id = usuario.getId();
        dto.email = usuario.getEmail();
        dto.nombre = usuario.getNombre();
        dto.apellidos = usuario.getApellidos();
        dto.administrador = usuario.isAdministrador();
        dto.rol = usuario.getRol();
        dto.githubId = usuario.getGithubId();
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }
}
