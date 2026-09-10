package com.arso.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerHelper {
	private static EntityManagerFactory entityManagerFactory;

	private static final ThreadLocal<EntityManager> entityManagerHolder;

	static {
		java.util.Map<String, String> properties = new java.util.HashMap<>();
		if (System.getenv("JDBC_URL") != null) properties.put("javax.persistence.jdbc.url", System.getenv("JDBC_URL"));
		if (System.getenv("JDBC_USER") != null) properties.put("javax.persistence.jdbc.user", System.getenv("JDBC_USER"));
		if (System.getenv("JDBC_PASSWORD") != null) properties.put("javax.persistence.jdbc.password", System.getenv("JDBC_PASSWORD"));
		
		entityManagerFactory = Persistence.createEntityManagerFactory("usuarios-mysql", properties);

		entityManagerHolder = new ThreadLocal<EntityManager>();

	}

	public static EntityManager getEntityManager() {

		EntityManager entityManager = entityManagerHolder.get();

		if (entityManager == null || !entityManager.isOpen()) {

			entityManager = entityManagerFactory.createEntityManager();

			entityManagerHolder.set(entityManager);

		}

		return entityManager;

	}

	public static void closeEntityManager() {

		EntityManager entityManager = entityManagerHolder.get();

		if (entityManager != null) {

			entityManagerHolder.set(null);

			entityManager.close();

		}

	}	
}
