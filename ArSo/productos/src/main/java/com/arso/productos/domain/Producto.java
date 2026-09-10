package com.arso.productos.domain;

import com.arso.repository.Identificable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table (name = "productos")
public class Producto implements Identificable {

    @Id
    @Column( name="id_producto" )
    private String id;
    @Column( name="titulo", nullable=false )
    private String titulo;
    @Column( name="descripcion", nullable=false, length=1000 )
    private String descripcion;
    @Column( name="precio", nullable=false )
    private Double precio;

    @Enumerated(EnumType.STRING)
    private EstadoProducto estado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacion;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    private Integer visualizaciones;
    private boolean envioDisponible;
    private boolean vendido;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "descripcion", column = @Column(name = "recogida_descripcion", length = 500)),
        @AttributeOverride(name = "longitud", column = @Column(name = "longitud")),
        @AttributeOverride(name = "latitud", column = @Column(name = "latitud"))
    })
    private LugarRecogida recogida;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private UsuarioResumen vendedor;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Integer getVisualizaciones() {
        return visualizaciones;
    }

    public void setVisualizaciones(Integer visualizaciones) {
        this.visualizaciones = visualizaciones;
    }

    public boolean isEnvioDisponible() {
        return envioDisponible;
    }

    public void setEnvioDisponible(boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }

    public boolean isVendido() {
        return vendido;
    }

    public void setVendido(boolean vendido) {
        this.vendido = vendido;
    }

    public LugarRecogida getRecogida() {
        return recogida;
    }

    public void setRecogida(LugarRecogida recogida) {
        this.recogida = recogida;
    }

    public void crearLugarRecogida(String descripcion, Double longitud, Double latitud) {
        LugarRecogida lugar = new LugarRecogida();
        lugar.setDescripcion(descripcion);
        lugar.setLongitud(longitud);
        lugar.setLatitud(latitud);
        this.recogida = lugar;
    }

    public UsuarioResumen getVendedor() {
        return vendedor;
    }

    public void setVendedor(UsuarioResumen vendedor) {
        this.vendedor = vendedor;
    }
}
