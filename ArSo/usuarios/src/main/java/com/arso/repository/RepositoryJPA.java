package com.arso.repository;

import com.arso.utils.EntityManagerHelper;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

public abstract class RepositoryJPA<T extends Identificable> implements Repository<T, String> {

    public abstract Class<T> getClase();

    @Override
    public String add(T entity) throws RepositoryException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // Generar ID si no existe
            if (entity.getId() == null || entity.getId().isEmpty()) {
                entity.setId(UUID.randomUUID().toString());
            }
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RepositoryException("Error al guardar la entidad", e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            EntityManagerHelper.closeEntityManager();
        }
        return entity.getId();
    }

    @Override
    public void update(T entity) throws RepositoryException, EntityNotFound {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            em.getTransaction().begin();
            T instancia = em.find(getClase(), entity.getId());
            if (instancia == null) {
                throw new EntityNotFound(entity.getId() + " no existe en el repositorio");
            }
            entity = em.merge(entity);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            throw new RepositoryException("Error al actualizar la entidad con id " + entity.getId(), e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public void delete(T entity) throws RepositoryException, EntityNotFound {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            em.getTransaction().begin();
            T instancia = em.find(getClase(), entity.getId());
            if (instancia == null) {
                throw new EntityNotFound(entity.getId() + " no existe en el repositorio");
            }
            em.remove(instancia);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            throw new RepositoryException("Error al borrar la entidad con id " + entity.getId(), e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public T getById(String id) throws EntityNotFound, RepositoryException {
        try {
            EntityManager em = EntityManagerHelper.getEntityManager();
            T instancia = em.find(getClase(), id);
            if (instancia == null) {
                throw new EntityNotFound(id + " no existe en el repositorio");
            } else {
                em.refresh(instancia);
            }
            return instancia;
        } catch (RuntimeException e) {
            throw new RepositoryException("Error al recuperar la entidad con id " + id, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public List<T> getAll() throws RepositoryException {
        try {
            EntityManager em = EntityManagerHelper.getEntityManager();
            final String queryString = " SELECT t from " + getClase().getSimpleName() + " t ";
            Query query = em.createQuery(queryString);
            query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
            return query.getResultList();
        } catch (RuntimeException e) {
            throw new RepositoryException("Error buscando todas las entidades de " + getClase().getSimpleName(), e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public List<String> getIds() throws RepositoryException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            final String queryString = " SELECT t.id from " + getClase().getSimpleName() + " t ";
            Query query = em.createQuery(queryString);
            query.setHint("javax.pesistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
            return query.getResultList();
        } catch (RuntimeException e) {
            throw new RepositoryException("Error buscando todos los ids de " + getClase().getSimpleName(), e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

}
