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

import com.increff.pos.pojo.InventoryPojo;

@Repository
public class InventoryDao extends AbstractDao{

	@PersistenceContext
	private EntityManager em;

	public List<InventoryPojo> selectAll(){
		InventoryPojo p = new InventoryPojo();
		return filter(p);
	}

	public List<InventoryPojo> filter(InventoryPojo p){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<InventoryPojo> cq = cb.createQuery(InventoryPojo.class);

		Root<InventoryPojo> root = cq.from(InventoryPojo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();


		if(p.getId() != 0)
			predicates.add(cb.equal(root.get("id"), p.getId()));
		if(p.getQuantity() != 0)
			predicates.add(cb.equal(root.get("quantity"), p.getQuantity()));
		for (Predicate predicate : predicates) {
			cq.where(cb.and(predicate));
		}

		TypedQuery<InventoryPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
}
