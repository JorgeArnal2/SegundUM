package com.arso.usuarios.dto;

import com.arso.usuarios.domain.Usuario;

import java.text.SimpleDateFormat;

public class UsuarioResponseDTO {

    private String id;
    private String nombre;
    private String apellidos;
    private String email;
    private String fechaNacimiento;
    private String telefono;
    private boolean administrador;
    private int contadorCompras;
    private int contadorVentas;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static UsuarioResponseDTO fromUsuario(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.id = usuario.getId();
        dto.nombre = usuario.getNombre();
        dto.apellidos = usuario.getApellidos();
        dto.email = usuario.getEmail();
        dto.fechaNacimiento = usuario.getFechaNacimiento() != null
            ? DATE_FORMAT.format(usuario.getFechaNacimiento()) : null;
        dto.telefono = usuario.getTelefono();
        dto.administrador = usuario.isAdministrador();
        dto.contadorCompras = usuario.getContadorCompras();
        dto.contadorVentas = usuario.getContadorVentas();
        dto.numeroValoracionesComoComprador = usuario.getNumeroValoracionesComoComprador();
        dto.numeroValoracionesComoVendedor = usuario.getNumeroValoracionesComoVendedor();
        dto.valoracionMediaComoComprador = usuario.getValoracionMediaComoComprador();
        dto.valoracionMediaComoVendedor = usuario.getValoracionMediaComoVendedor();
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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
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
