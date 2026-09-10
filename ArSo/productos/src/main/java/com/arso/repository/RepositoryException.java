package com.arso.repository;

/*
 * Excepción que representa un fallo en el sistema de persistencia.
 * Al instanciarla, se establece la excepción interna que produce el error (causa).
 */

@SuppressWarnings("serial")
public class RepositoryException extends Exception {

	public RepositoryException(String msg, Throwable causa) {
		super(msg, causa);
	}
	
	public RepositoryException(String msg) {
		super(msg);		
	}
	
		
}
