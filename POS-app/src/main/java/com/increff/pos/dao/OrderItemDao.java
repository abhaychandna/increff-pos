package com.increff.pos.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.ApiException;

@Repository
public class OrderItemDao extends AbstractDao {
	public List<OrderItemPojo> selectByOrderId(Integer id) throws ApiException {
        return selectMultiple(OrderItemPojo.class, "orderId", id);
	}
}
