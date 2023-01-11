package com.increff.pos.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;
@Repository
public class BrandDao extends AbstractDao{

	
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

	public List<BrandPojo> filter(BrandPojo brand){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BrandPojo> cq = cb.createQuery(BrandPojo.class);

		Root<BrandPojo> root = cq.from(BrandPojo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		if(brand.getId() != 0)
			predicates.add(cb.equal(root.get("id"), brand.getId()));
		if(Objects.nonNull(brand.getBrand()))
			predicates.add(cb.equal(root.get("brand"), brand.getBrand()));
		if(Objects.nonNull(brand.getCategory()))
			predicates.add(cb.equal(root.get("category"), brand.getCategory()));
		cq.where(cb.and(predicates.toArray(Predicate[]::new)));

		TypedQuery<BrandPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
	
}
