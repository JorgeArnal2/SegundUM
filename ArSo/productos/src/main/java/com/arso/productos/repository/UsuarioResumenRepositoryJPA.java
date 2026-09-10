package com.arso.productos.repository;

import com.arso.productos.domain.UsuarioResumen;
import com.arso.repository.EntityNotFound;
import com.arso.repository.RepositoryException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

@Repository
public class UsuarioResumenRepositoryJPA {

    @PersistenceContext
    private EntityManager em;

    public UsuarioResumen getById(String id) throws RepositoryException, EntityNotFound {
        UsuarioResumen usuario = em.find(UsuarioResumen.class, id);
        if (usuario == null) {
            throw new EntityNotFound(id + " no existe en el repositorio");
        }
        return usuario;
    }

    public List<UsuarioResumen> findAll() {
        return em.createQuery("SELECT u FROM UsuarioResumen u", UsuarioResumen.class).getResultList();
    }

    @Transactional
    public UsuarioResumen save(UsuarioResumen usuario) {
        if (usuario.getId() == null || usuario.getId().isEmpty()) {
            usuario.setId(UUID.randomUUID().toString());
            em.persist(usuario);
        } else {
            usuario = em.merge(usuario);
        }
        return usuario;
    }

    @Transactional
    public void delete(UsuarioResumen usuario) {
        UsuarioResumen instancia = em.find(UsuarioResumen.class, usuario.getId());
        if (instancia != null) {
            em.remove(instancia);
        }
    }

    public long count() {
        return em.createQuery("SELECT COUNT(u) FROM UsuarioResumen u", Long.class).getSingleResult();
    }
}
