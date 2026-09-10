package com.arso.repository;

import java.util.List;

/*
 *  Repositorio para entidades gestionadas con identificador.
 *  El parámetro T representa el tipo de datos de la entidad.
 *  El parámetro K es el tipo del identificador.
 */

public interface Repository<T, K> {

	K add(T entity) throws RepositoryException;

	void update(T entity) throws RepositoryException, EntityNotFound;

	void delete(T entity) throws RepositoryException, EntityNotFound;

	T getById(K id) throws RepositoryException, EntityNotFound;

	List<T> getAll() throws RepositoryException;

	List<K> getIds() throws RepositoryException;

}