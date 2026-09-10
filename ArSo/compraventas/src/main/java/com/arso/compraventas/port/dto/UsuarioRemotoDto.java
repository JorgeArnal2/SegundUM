package com.arso.compraventas.port.dto;

/**
 * DTO con los campos del microservicio Usuarios que necesita Compraventas.
 */
public class UsuarioRemotoDto {

    private String id;
    private String nombre;
    private String apellidos;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombreCompleto() {
        if (apellidos != null && !apellidos.isBlank()) {
            return nombre + " " + apellidos;
        }
        return nombre;
    }
}
