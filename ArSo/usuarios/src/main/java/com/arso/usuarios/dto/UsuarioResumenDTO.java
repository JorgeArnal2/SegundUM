package com.arso.usuarios.dto;

import com.arso.usuarios.domain.Usuario;

import javax.ws.rs.core.UriInfo;

public class UsuarioResumenDTO {

    private String id;
    private String nombre;
    private String apellidos;
    private String email;
    private int contadorCompras;
    private int contadorVentas;
    private String href;

    public static UsuarioResumenDTO fromUsuario(Usuario usuario, UriInfo uriInfo) {
        UsuarioResumenDTO dto = new UsuarioResumenDTO();
        dto.id = usuario.getId();
        dto.nombre = usuario.getNombre();
        dto.apellidos = usuario.getApellidos();
        dto.email = usuario.getEmail();
        dto.contadorCompras = usuario.getContadorCompras();
        dto.contadorVentas = usuario.getContadorVentas();
        dto.numeroValoracionesComoComprador = usuario.getNumeroValoracionesComoComprador();
        dto.numeroValoracionesComoVendedor = usuario.getNumeroValoracionesComoVendedor();
        dto.valoracionMediaComoComprador = usuario.getValoracionMediaComoComprador();
        dto.valoracionMediaComoVendedor = usuario.getValoracionMediaComoVendedor();
        dto.href = uriInfo.getBaseUriBuilder()
            .path("usuarios")
            .path(usuario.getId())
            .build()
            .toString();
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getContadorCompras() {
        return contadorCompras;
    }

    public void setContadorCompras(int contadorCompras) {
        this.contadorCompras = contadorCompras;
    }

    public int getContadorVentas() {
        return contadorVentas;
    }

    public void setContadorVentas(int contadorVentas) {
        this.contadorVentas = contadorVentas;
    }

    private int numeroValoracionesComoComprador;
    private int numeroValoracionesComoVendedor;
    private double valoracionMediaComoComprador;
    private double valoracionMediaComoVendedor;

    public int getNumeroValoracionesComoComprador() {
        return numeroValoracionesComoComprador;
    }

    public void setNumeroValoracionesComoComprador(int numeroValoracionesComoComprador) {
        this.numeroValoracionesComoComprador = numeroValoracionesComoComprador;
    }

    public int getNumeroValoracionesComoVendedor() {
        return numeroValoracionesComoVendedor;
    }

    public void setNumeroValoracionesComoVendedor(int numeroValoracionesComoVendedor) {
        this.numeroValoracionesComoVendedor = numeroValoracionesComoVendedor;
    }

    public double getValoracionMediaComoComprador() {
        return valoracionMediaComoComprador;
    }

    public void setValoracionMediaComoComprador(double valoracionMediaComoComprador) {
        this.valoracionMediaComoComprador = valoracionMediaComoComprador;
    }

    public double getValoracionMediaComoVendedor() {
        return valoracionMediaComoVendedor;
    }

    public void setValoracionMediaComoVendedor(double valoracionMediaComoVendedor) {
        this.valoracionMediaComoVendedor = valoracionMediaComoVendedor;
    }
}
