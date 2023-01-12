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

import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.ApiException;

@Repository
public class OrderItemDao extends AbstractDao {
	public List<OrderItemPojo> selectByOrderId(Integer id) throws ApiException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderItemPojo> cq = cb.createQuery(OrderItemPojo.class);
        Root<OrderItemPojo> root = cq.from(OrderItemPojo.class);
       	cq.where(cb.equal(root.get("orderId"), id));
        TypedQuery<OrderItemPojo> query = em.createQuery(cq);
		return query.getResultList();
	}
}
