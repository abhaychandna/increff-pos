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

import com.increff.pos.model.ProductSearchForm;
import com.increff.pos.pojo.ProductPojo;

@Repository
public class ProductDao extends AbstractDao{

	@PersistenceContext
	private EntityManager em;

	public List<ProductPojo> selectAll(){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);
		Root<ProductPojo> root = cq.from(ProductPojo.class);
		CriteriaQuery<ProductPojo> all = cq.select(root);
		TypedQuery<ProductPojo> query = em.createQuery(all);
        return query.getResultList();
	}

	public ProductPojo getByBarcode(String barcode) throws ApiException{
		ProductSearchForm p = new ProductSearchForm();
		p.setBarcode(barcode);
		List<ProductPojo> list = filter(p);
		if(list.size() == 0)
			throw new ApiException("Product does not exist with barcode: " + barcode);
		return list.get(0);
	}

	public List<ProductPojo> filter(ProductSearchForm p){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);

		Root<ProductPojo> root = cq.from(ProductPojo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		if(p.getId() != null)
			predicates.add(cb.equal(root.get("id"), p.getId()));
		if(p.getBarcode() != null)
			predicates.add(cb.equal(root.get("barcode"), p.getBarcode()));
		if(p.getName() != null)
			predicates.add(cb.equal(root.get("name"), p.getName()));
		if(p.getBrand_category() != 0)
			predicates.add(cb.equal(root.get("brand_category"), p.getBrand_category()));
		cq.where(cb.and(predicates.toArray(Predicate[]::new)));


		TypedQuery<ProductPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
}
