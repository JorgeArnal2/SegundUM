package com.arso.productos.domain;

import com.arso.repository.Identificable;

import javax.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioResumen implements Identificable {

    @Id
    @Column(name = "id_usuario")
    private String id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Override
    public String getId() {
        return id;
    }

    @Override
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
}
