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



	public List<InventoryPojo> selectAll(){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<InventoryPojo> cq = cb.createQuery(InventoryPojo.class);
		Root<InventoryPojo> root = cq.from(InventoryPojo.class);
		CriteriaQuery<InventoryPojo> all = cq.select(root);
		TypedQuery<InventoryPojo> query = em.createQuery(all);
        return query.getResultList();
	}
}
