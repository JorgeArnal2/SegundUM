package com.arso.repository;

/*
 * Excepción notificada si no existe una entidad con el identificador
 * proporcionado en el repositorio.
 */

@SuppressWarnings("serial")
public class EntityNotFound extends Exception {

		
	public EntityNotFound(String msg) {
		super(msg);		
	}
	
		
}
