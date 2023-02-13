package com.increff.pos.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.util.ApiException;

@Repository
public class OrderItemDao extends AbstractDao<OrderItemPojo> {

	OrderItemDao() {
		super(OrderItemPojo.class);
	}

}
