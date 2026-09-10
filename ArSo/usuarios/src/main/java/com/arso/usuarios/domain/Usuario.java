package com.arso.usuarios.domain;

import com.arso.repository.Identificable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table( name="usuarios" )
public class Usuario implements Identificable {

    @Id
    @Column( name="id_usuario" )
    private String id;

    @Column(name="email", nullable=false, unique=true)
    private String email;
    @Column(name="nombre", nullable=false)
    private String nombre;
    @Column(name="apellidos", nullable=false)
    private String apellidos;
    @Column(name="clave", nullable=false)
    private String clave;
    @Temporal(TemporalType.DATE)
    @Column(name="fecha_nacimiento", nullable=false)
    private Date fechaNacimiento;
    @Column(name="telefono", nullable=false)
    private String telefono;
    @Column(name="administrador", nullable=false)
    private boolean administrador;
    @Column(name="rol", nullable=false)
    private String rol;
    @Column(name="github_id", unique = true)
    private String githubId;
    @Column(name="contador_compras", nullable=false)
    private int contadorCompras;
    @Column(name="contador_ventas", nullable=false)
    private int contadorVentas;

    @Column(name="num_valoraciones_comprador")
    private int numeroValoracionesComoComprador = 0;
    @Column(name="num_valoraciones_vendedor")
    private int numeroValoracionesComoVendedor = 0;
    @Column(name="val_media_comprador")
    private double valoracionMediaComoComprador = 0.0;
    @Column(name="val_media_vendedor")
    private double valoracionMediaComoVendedor = 0.0;

    // Getters and setters

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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
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
