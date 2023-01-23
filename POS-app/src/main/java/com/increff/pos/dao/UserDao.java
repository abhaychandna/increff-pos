package com.increff.pos.dao;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends AbstractDao {

	private static String delete_id = "delete from UserPojo p where id=:id";

	public Integer delete(Integer id) {
		Query query = em().createQuery(delete_id);
		query.setParameter("id", id);
		return query.executeUpdate();
	}
}
