package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderItemService {

	@Autowired
	private OrderItemDao dao;

	public List<OrderItemPojo> add(List<OrderItemPojo> items) throws ApiException {
		for (OrderItemPojo item : items) {
			dao.insert(item);
		}
		return items;
	}

	public OrderItemPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	public List<OrderItemPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, OrderItemPojo.class);
	}

	public OrderItemPojo getCheck(Integer id) throws ApiException {
		OrderItemPojo item = dao.select(OrderItemPojo.class, id);
		if (Objects.isNull(item)) {
			throw new ApiException("Order with given ID does not exist, id: " + id);
		}
		return item;
	}

	public List<OrderItemPojo> getByOrderId(Integer id) throws ApiException {
		return dao.selectByOrderId(id);
	}
	
	public Integer getRecordsCount() {
		return dao.getRecordsCount(OrderItemPojo.class);
	}
	public <T> List<OrderItemPojo> getByColumn(String column, List<T> values){
		return dao.selectByColumn(OrderItemPojo.class, column, values);
	}

	public <T> List<OrderItemPojo> getByMultipleColumns(List<String> columns, List<List<T>> values){
		return dao.selectByMultipleColumns(OrderItemPojo.class, columns, values);
	}
}
