package com.increff.pos.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.ProductPojo;

@Repository
public class ProductDao extends AbstractDao{

	@PersistenceContext
	private EntityManager em;

	public List<ProductPojo> selectAll(){
		ProductPojo p = new ProductPojo();
		return filter(p);
	}

	public List<ProductPojo> filter(ProductPojo p){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);

		Root<ProductPojo> root = cq.from(ProductPojo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		System.out.println("id: " + p.getId());
		System.out.println("barcode: " + p.getBarcode());
		System.out.println("name: " + p.getName());
		System.out.println("mrp: " + p.getMrp());
		System.out.println("brand_category: " + p.getBrand_category());

		if(p.getId() != 0)
			predicates.add(cb.equal(root.get("id"), p.getId()));
		if(p.getBarcode() != null)
			predicates.add(cb.equal(root.get("barcode"), p.getBarcode()));
		if(p.getName() != null)
			predicates.add(cb.equal(root.get("name"), p.getName()));
		if(p.getMrp() != 0.0)
			predicates.add(cb.equal(root.get("mrp"), p.getMrp()));
		if(p.getBrand_category() != 0)
			predicates.add(cb.equal(root.get("brand_category"), p.getBrand_category()));

		for (Predicate predicate : predicates) {
			cq.where(cb.and(predicate));
		}

		TypedQuery<ProductPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
}
