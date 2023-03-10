package com.increff.pos.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class AbstractDao<T> {

	private Class<T> clazz;

	AbstractDao(Class<T> clazz) {
		this.clazz = clazz;
	}

	@PersistenceContext
	protected EntityManager em;

	public T insert(T b) {
		em.persist(b);
		return b;
	}

	public <R> T select(R id) {
		return em.find(clazz, id);
	}

	public List<T> selectAll(Integer pageNo, Integer pageSize) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		CriteriaQuery<T> all = cq.select(root);
		TypedQuery<T> query = em.createQuery(all);
		query.setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
		return query.getResultList();
	}
	public List<T> selectAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		CriteriaQuery<T> all = cq.select(root);
		TypedQuery<T> query = em.createQuery(all);
		return query.getResultList();
	}

	public Integer getRecordsCount() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(clazz);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public <R> List<T> selectMultiple(String column, R value) {
		return selectByMultipleColumns(List.of(column), List.of(List.of(value)));
	}

	public <R> T selectByColumn(String column, R value) {
		return getSingle(selectByMultipleColumns(List.of(column), List.of(List.of(value))));
	}

	public <R> List<T> selectByColumn(String column, List<R> values) {
		return selectByMultipleColumns(List.of(column), List.of(values));
	}

	public <R> List<T> selectByMultipleColumns(List<String> columns, List<List<R>> values) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);

		List<Predicate> preds = new ArrayList<>();
		for(int i=0; i<columns.size(); i++){
			In<R> inClause = cb.in(root.get(columns.get(i)));
			if(values.get(i).isEmpty()) return Collections.emptyList();
			for (R val : values.get(i)) {
				inClause.value(val);
			}
			preds.add(inClause);
		}
		cq.where(cb.and(preds.toArray(new Predicate[] {})));
		TypedQuery<T> query = em.createQuery(cq);
		return query.getResultList();
	}

	private <T> T getSingle(List<T> list) {
		return list.stream().findFirst().orElse(null);
	}
}
