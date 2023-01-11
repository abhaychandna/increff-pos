package com.increff.pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public abstract class AbstractDao {
	
	@PersistenceContext
	protected EntityManager em;

	protected <T> T getSingle(TypedQuery<T> query) {
		return query.getResultList().stream().findFirst().orElse(null);
	}
	
	protected <T> TypedQuery<T> getQuery(String jpql, Class<T> clazz) {
		return em.createQuery(jpql, clazz);
	}
	
	protected EntityManager em() {
		return em;
	}

	public <T> void insert(T b) {
		em.persist(b);
	}

	public <T> T select(Class<T> c, int id) {
		return em.find(c, id);
	}

	public <T> void update(T b) {
	}

}
