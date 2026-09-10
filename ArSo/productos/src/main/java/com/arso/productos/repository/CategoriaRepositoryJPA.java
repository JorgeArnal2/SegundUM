package com.arso.productos.repository;

import com.arso.productos.domain.Categoria;
import com.arso.repository.EntityNotFound;
import com.arso.repository.RepositoryException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

@Repository
public class CategoriaRepositoryJPA implements CategoriaRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Categoria findById(String id) {
        return em.find(Categoria.class, id);
    }

    @Override
    public List<Categoria> findRootCategories() {
        return em.createQuery("SELECT c FROM Categoria c WHERE c.parent IS NULL", Categoria.class)
            .getResultList();
    }

    @Override
    public List<Categoria> findAll() {
        return em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
    }

    @Override
    @Transactional
    public Categoria save(Categoria categoria) {
        if (categoria.getId() == null || categoria.getId().isEmpty()) {
            categoria.setId(UUID.randomUUID().toString());
            em.persist(categoria);
        } else {
            categoria = em.merge(categoria);
        }
        return categoria;
    }

    @Override
    @Transactional
    public void delete(Categoria categoria) {
        Categoria instancia = em.find(Categoria.class, categoria.getId());
        if (instancia != null) {
            em.remove(instancia);
        }
    }

    public Categoria getById(String id) throws RepositoryException, EntityNotFound {
        List<Categoria> results = em.createQuery(
                "SELECT c FROM Categoria c LEFT JOIN FETCH c.subcategorias WHERE c.id = :id",
                Categoria.class)
            .setParameter("id", id)
            .getResultList();
        if (results.isEmpty()) {
            throw new EntityNotFound(id + " no existe en el repositorio");
        }
        return results.get(0);
    }
}
