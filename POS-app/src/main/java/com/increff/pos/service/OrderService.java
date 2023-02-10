package com.increff.pos.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

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
	@Autowired
	private OrderItemService orderItemService;

	public List<OrderItemPojo> add(List<OrderItemPojo> items) throws ApiException {
		if(items.isEmpty()) throw new ApiException("No items in order");		

		OrderPojo order = new OrderPojo();
		order.setTime(ZonedDateTime.now());
		dao.insert(order);

		for (OrderItemPojo item : items) {
			item.setOrderId(order.getId());
		}
		orderItemService.add(items);
		return items;
	}

	public List<OrderPojo> filterByDate(ZonedDateTime start, ZonedDateTime end) {
		return dao.filterByDate(start, end);
	}

	public List<OrderPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize);
	}

	public OrderPojo getCheck(Integer id) throws ApiException {
		OrderPojo order = dao.select(id);
		if (Objects.isNull(order))
			throw new ApiException("Order does not exist with id: " + id);
		return order;
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount();
	}


}
