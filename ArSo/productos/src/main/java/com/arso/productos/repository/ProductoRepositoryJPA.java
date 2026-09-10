package com.arso.productos.repository;

import com.arso.productos.domain.EstadoProducto;
import com.arso.productos.domain.Producto;
import com.arso.repository.EntityNotFound;
import com.arso.repository.RepositoryException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ProductoRepositoryJPA implements ProductoRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public String add(Producto producto) throws RepositoryException {
        if (producto.getId() == null || producto.getId().isEmpty()) {
            producto.setId(UUID.randomUUID().toString());
        }
        try {
            em.persist(producto);
        } catch (Exception e) {
            throw new RepositoryException("Error al guardar el producto", e);
        }
        return producto.getId();
    }

    @Override
    @Transactional
    public void update(Producto producto) throws RepositoryException, EntityNotFound {
        if (em.find(Producto.class, producto.getId()) == null) {
            throw new EntityNotFound(producto.getId() + " no existe en el repositorio");
        }
        try {
            em.merge(producto);
        } catch (Exception e) {
            throw new RepositoryException("Error al actualizar el producto", e);
        }
    }

    @Override
    @Transactional
    public void delete(Producto producto) throws RepositoryException, EntityNotFound {
        Producto instancia = em.find(Producto.class, producto.getId());
        if (instancia == null) {
            throw new EntityNotFound(producto.getId() + " no existe en el repositorio");
        }
        try {
            em.remove(instancia);
        } catch (Exception e) {
            throw new RepositoryException("Error al eliminar el producto", e);
        }
    }

    @Override
    public Producto getById(String id) throws RepositoryException, EntityNotFound {
        Producto producto = em.find(Producto.class, id);
        if (producto == null) {
            throw new EntityNotFound(id + " no existe en el repositorio");
        }
        return producto;
    }

    @Override
    public List<Producto> getAll() throws RepositoryException {
        return em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();
    }

    @Override
    public List<String> getIds() throws RepositoryException {
        return em.createQuery("SELECT p.id FROM Producto p", String.class).getResultList();
    }

    @Override
    public Producto findById(String id) {
        return em.find(Producto.class, id);
    }

    @Override
    public List<Producto> findAll() {
        return em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        if (producto.getId() == null || producto.getId().isEmpty()) {
            producto.setId(UUID.randomUUID().toString());
            em.persist(producto);
        } else {
            producto = em.merge(producto);
        }
        return producto;
    }

    @Override
    @Transactional
    public void deleteProducto(Producto producto) {
        Producto instancia = em.find(Producto.class, producto.getId());
        if (instancia != null) {
            em.remove(instancia);
        }
    }

    @Override
    public List<Producto> findBy(String rutaCategoria, String description, EstadoProducto estado, Double maxPrice) {
        StringBuilder queryString = new StringBuilder("SELECT p FROM Producto p WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (rutaCategoria != null && !rutaCategoria.trim().isEmpty()) {
            queryString.append(" AND p.categoria.ruta LIKE :rutaCategoria");
            parameters.put("rutaCategoria", rutaCategoria + "%");
        }
        if (description != null && !description.trim().isEmpty()) {
            queryString.append(" AND p.descripcion LIKE :description");
            parameters.put("description", "%" + description + "%");
        }
        if (estado != null) {
            queryString.append(" AND p.estado >= :estado");
            parameters.put("estado", estado);
        }
        if (maxPrice != null) {
            queryString.append(" AND p.precio <= :maxPrice");
            parameters.put("maxPrice", maxPrice);
        }

        TypedQuery<Producto> query = em.createQuery(queryString.toString(), Producto.class);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    @Override
    public List<Producto> findHistorialMes(int mes, int anio) {
        Calendar cal = Calendar.getInstance();
        cal.set(anio, mes - 1, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date primerDia = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date ultimoDia = cal.getTime();

        return em.createQuery(
            "SELECT p FROM Producto p WHERE p.fechaPublicacion BETWEEN :primerDia AND :ultimoDia ORDER BY p.visualizaciones DESC",
            Producto.class)
            .setParameter("primerDia", primerDia)
            .setParameter("ultimoDia", ultimoDia)
            .getResultList();
    }

    @Override
    public List<Producto> findByVendedor(String idVendedor) {
        return em.createQuery(
            "SELECT p FROM Producto p WHERE p.vendedor.id = :idVendedor ORDER BY p.fechaPublicacion DESC",
            Producto.class)
            .setParameter("idVendedor", idVendedor)
            .getResultList();
    }
}
