package com.increff.pos.dao;

import java.util.ArrayList;
import java.util.List;

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
		BrandPojo p = new BrandPojo();
		return filter(p);
	}

	public List<BrandPojo> getByBrandCategory(String brand, String category) {
		BrandPojo p = new BrandPojo();
		p.setBrand(brand);
		p.setCategory(category);
		return filter(p);
	}

	public List<BrandPojo> filter(BrandPojo p){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BrandPojo> cq = cb.createQuery(BrandPojo.class);

		Root<BrandPojo> root = cq.from(BrandPojo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		if(p.getId() != 0)
			predicates.add(cb.equal(root.get("id"), p.getId()));
		if(p.getBrand() != null)
			predicates.add(cb.equal(root.get("brand"), p.getBrand()));
		if(p.getCategory() != null)
			predicates.add(cb.equal(root.get("category"), p.getCategory()));
		cq.where(cb.and(predicates.toArray(Predicate[]::new)));

		TypedQuery<BrandPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
	
}
