package com.increff.pos.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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

	public <T> T select(Class<T> c, Integer id) {
		return em.find(c, id);
	}

	public <T> List<T> selectAll(Integer pageNo, Integer pageSize, Class<T> pojo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(pojo);
		Root<T> root = cq.from(pojo);
		CriteriaQuery<T> all = cq.select(root);
		TypedQuery<T> query = em.createQuery(all);
		query.setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
		return query.getResultList();
	}

	public <T> void update(T b) {
	}

}
