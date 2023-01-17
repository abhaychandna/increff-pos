package com.increff.pos.dao;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.OrderPojo;

@Repository
public class OrderDao extends AbstractDao {
    public List<OrderPojo> filterByDate(ZonedDateTime start, ZonedDateTime end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);

		Root<OrderPojo> root = cq.from(OrderPojo.class);
		Predicate startPredicate = cb.greaterThanOrEqualTo(root.<ZonedDateTime>get("time"), start);
		Predicate endPredicate = cb.lessThanOrEqualTo(root.<ZonedDateTime>get("time"), end);
        cq.where(startPredicate, endPredicate);

        TypedQuery<OrderPojo> query = em.createQuery(cq);
        return query.getResultList();
    }
}
