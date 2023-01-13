package com.increff.pos.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.increff.pos.model.ProductSearchForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

@Repository
public class ProductDao extends AbstractDao{

	public ProductPojo getByBarcode(String barcode) throws ApiException{
		ProductSearchForm p = new ProductSearchForm();
		p.setBarcode(barcode);
		List<ProductPojo> list = filter(p);
		if(list.size() == 0)
			throw new ApiException("Product does not exist with barcode: " + barcode);
		return list.get(0);
	}

	public List<ProductPojo> filter(ProductSearchForm searchForm){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);

		Root<ProductPojo> root = cq.from(ProductPojo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		if(Objects.nonNull(searchForm.getBarcode()))
			predicates.add(cb.equal(root.get("barcode"), searchForm.getBarcode()));
		if(Objects.nonNull(searchForm.getBrandCategory()))
			predicates.add(cb.equal(root.get("brandCategory"), searchForm.getBrandCategory()));
		cq.where(cb.and(predicates.toArray(Predicate[]::new)));


		TypedQuery<ProductPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
}
