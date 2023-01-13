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
}
