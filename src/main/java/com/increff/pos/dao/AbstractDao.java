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

	public <T> T insert(T b) {
		em.persist(b);
		return b;
	}

	public <T,R> T select(Class<T> c, R id) {
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
	public <T> List<T> selectAll(Class<T> pojo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(pojo);
		Root<T> root = cq.from(pojo);
		CriteriaQuery<T> all = cq.select(root);
		TypedQuery<T> query = em.createQuery(all);
		return query.getResultList();
	}

	public <T> Integer getRecordsCount(Class<T> pojo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(pojo);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public <T> void update(T b) {
	}

	public <T> List<T> selectMultiple(Class<T> c, String column, String value) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(c);
		Root<T> root = cq.from(c);
		cq.where(cb.equal(root.get(column), value));
		TypedQuery<T> query = em.createQuery(cq);
		return query.getResultList();
	}

	public <T> T selectByColumn(Class<T> c, String column, String value) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(c);
		Root<T> root = cq.from(c);
		cq.where(cb.equal(root.get(column), value));
		TypedQuery<T> query = em.createQuery(cq);
		return query.getSingleResult();
	}
}
