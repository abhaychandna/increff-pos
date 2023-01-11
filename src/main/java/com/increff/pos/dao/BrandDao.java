package com.increff.pos.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;
@Repository
public class BrandDao extends AbstractDao{
	@PersistenceContext
	private EntityManager em;
	
	public List<BrandPojo> selectAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BrandPojo> cq = cb.createQuery(BrandPojo.class);
		Root<BrandPojo> root = cq.from(BrandPojo.class);
		CriteriaQuery<BrandPojo> all = cq.select(root);
		TypedQuery<BrandPojo> query = em.createQuery(all);
        return query.getResultList();
	}

	public List<BrandPojo> getByBrandCategory(String brand, String category) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BrandPojo> cq = cb.createQuery(BrandPojo.class);

		Root<BrandPojo> root = cq.from(BrandPojo.class);
		Predicate brandPredicate = cb.equal(root.get("brand"), brand);
		Predicate categoryPredicate = cb.equal(root.get("category"), category);
        cq.where(brandPredicate, categoryPredicate);

        TypedQuery<BrandPojo> query = em.createQuery(cq);
        return query.getResultList();
	}

	public List<BrandPojo> filter(BrandPojo p){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BrandPojo> cq = cb.createQuery(BrandPojo.class);

		Root<BrandPojo> root = cq.from(BrandPojo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		if(p.getId() != 0)
			predicates.add(cb.equal(root.get("id"), p.getId()));
		if(Objects.nonNull(p.getBrand()))
			predicates.add(cb.equal(root.get("brand"), p.getBrand()));
		if(Objects.nonNull(p.getCategory()))
			predicates.add(cb.equal(root.get("category"), p.getCategory()));
		cq.where(cb.and(predicates.toArray(Predicate[]::new)));

		TypedQuery<BrandPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
	
}
