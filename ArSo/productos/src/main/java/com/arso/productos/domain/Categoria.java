package com.arso.productos.domain;

import com.arso.repository.Identificable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@XmlRootElement(name = "categoria")
@XmlAccessorType(XmlAccessType.FIELD)
@Table (name = "categorias")
public class Categoria implements Identificable {

    @Id
    @XmlAttribute(name = "id")
    @Column (name="id_categoria" )
    private String id;

    @XmlElement
    @Column (name="nombre", nullable=false)
    private String nombre;
    @XmlElement
    @Column (name="descripcion", nullable=true, length=1000)
    private String descripcion;
    @XmlAttribute(name = "ruta")
    @Column (name="ruta", nullable=false, unique=true, length=500)
    private String ruta;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @XmlTransient
    private Categoria parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @XmlElement(name = "categoria")
    private List<Categoria> subcategorias = new ArrayList<>();

    // Getters and setters

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Categoria getParent() {
        return parent;
    }

    public void setParent(Categoria parent) {
        this.parent = parent;
    }

    public List<Categoria> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<Categoria> subcategorias) {
        this.subcategorias = subcategorias;
    }

    public void addSubcategoria(Categoria child) {
        subcategorias.add(child);
        child.setParent(this);
    }
}
