package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderService {

	@Autowired
	private OrderDao dao;

	public OrderPojo add(OrderPojo order) throws ApiException {
		dao.insert(order);
		return order;
	}

	public OrderPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	public List<OrderPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, OrderPojo.class);
	}

	private OrderPojo getCheck(Integer id) throws ApiException {
		OrderPojo order = dao.select(OrderPojo.class, id);
		if (order == null)
			throw new ApiException("Order does not exist with id: " + id);
		return order;
	}


}
