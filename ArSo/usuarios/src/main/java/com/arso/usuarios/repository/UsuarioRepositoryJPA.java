package com.arso.usuarios.repository;

import com.arso.usuarios.domain.Usuario;
import com.arso.repository.EntityNotFound;
import com.arso.repository.RepositoryException;
import com.arso.repository.RepositoryJPA;
import com.arso.utils.EntityManagerHelper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class UsuarioRepositoryJPA extends RepositoryJPA<Usuario> implements UsuarioRepository {

    @Override
    public Class<Usuario> getClase() {
        return Usuario.class;
    }

    @Override
    public Usuario findById(String id) {
        try {
            return getById(id);
        } catch (EntityNotFound | RepositoryException e) {
            return null;
        }
    }

    @Override
    public Usuario findByEmailAndClave(String email, String clave) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :email AND u.clave = :clave",
                    Usuario.class);
            query.setParameter("email", email);
            query.setParameter("clave", clave);
            List<Usuario> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public List<Usuario> findAll() {
        try {
            return getAll();
        } catch (RepositoryException e) {
            throw new RuntimeException("Error al obtener todos los usuarios", e);
        }
    }

    @Override
    public Usuario save(Usuario usuario) {
        try {
            if (usuario.getId() == null || usuario.getId().isEmpty()) {
                add(usuario);
            } else {
                update(usuario);
            }
            return usuario;
        } catch (RepositoryException | EntityNotFound e) {
            throw new RuntimeException("Error al guardar el usuario", e);
        }
    }

    @Override
    public void delete(Usuario usuario) {
        try {
            super.delete(usuario);
        } catch (RepositoryException | EntityNotFound e) {
            throw new RuntimeException("Error al eliminar el usuario", e);
        }
    }

    @Override
    public boolean existeEmail(String email) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public Usuario obtenerPorEmail(String email) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email",
                Usuario.class);
            query.setParameter("email", email);
            List<Usuario> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public Usuario obtenerPorGithubId(String githubId) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.githubId = :githubId",
                Usuario.class);
            query.setParameter("githubId", githubId);
            List<Usuario> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}
