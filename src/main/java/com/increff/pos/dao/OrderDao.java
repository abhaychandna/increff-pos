package com.increff.pos.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.OrderPojo;

@Repository
public class OrderDao extends AbstractDao {

	public List<OrderPojo> filter(OrderPojo p) {
		return null;
	}

}
