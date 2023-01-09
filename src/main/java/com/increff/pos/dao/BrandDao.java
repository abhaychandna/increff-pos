package com.increff.pos.dao;

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
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BrandPojo> cq = cb.createQuery(BrandPojo.class);

        Root<BrandPojo> rootEntry = cq.from(BrandPojo.class);

		CriteriaQuery<BrandPojo> all = cq.select(rootEntry);
		
		TypedQuery<BrandPojo> query = em.createQuery(all);
		return query.getResultList();
	}

	public List<BrandPojo> getByBrandCategory(String brand, String category) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BrandPojo> cq = cb.createQuery(BrandPojo.class);

        Root<BrandPojo> BrandPojo = cq.from(BrandPojo.class);

       	Predicate brandPredicate = cb.equal(BrandPojo.get("brand"), brand);
		Predicate categoryPredicate = cb.equal(BrandPojo.get("category"), category);
		cq.where(brandPredicate, categoryPredicate);

        TypedQuery<BrandPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
	
}
