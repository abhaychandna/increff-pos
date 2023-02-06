package com.increff.pos.dao;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.DaySalesPojo;

@Repository
public class DaySalesDao extends AbstractDao<DaySalesPojo> {
    
    DaySalesDao() {
        super(DaySalesPojo.class);
    }

    public List<DaySalesPojo> filterByDate(Integer pageNo, Integer pageSize, ZonedDateTime start, ZonedDateTime end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DaySalesPojo> cq = cb.createQuery(DaySalesPojo.class);

		Root<DaySalesPojo> root = cq.from(DaySalesPojo.class);
		Predicate startPredicate = cb.greaterThanOrEqualTo(root.<ZonedDateTime>get("date"), start);
		Predicate endPredicate = cb.lessThanOrEqualTo(root.<ZonedDateTime>get("date"), end);
        cq.where(startPredicate, endPredicate);

        TypedQuery<DaySalesPojo> query = em.createQuery(cq);
        query.setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
        return query.getResultList();
    }
}
