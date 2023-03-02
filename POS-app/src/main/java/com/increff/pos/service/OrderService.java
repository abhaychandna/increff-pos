package com.increff.pos.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.util.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderService {

	@Autowired
	private OrderDao orderDao;

	public List<OrderItemPojo> add(List<OrderItemPojo> items) throws ApiException {
		if(items.isEmpty()) throw new ApiException("No items in order");		

		OrderPojo order = new OrderPojo();
		order.setTime(ZonedDateTime.now());
		orderDao.insert(order);

		for (OrderItemPojo item : items) {
			item.setOrderId(order.getId());
		}
		addItem(items);
		return items;
	}

	public List<OrderPojo> filterByDate(ZonedDateTime start, ZonedDateTime end) {
		return orderDao.filterByDate(start, end);
	}

	public List<OrderPojo> getAll(Integer pageNo, Integer pageSize) {
		return orderDao.selectAll(pageNo, pageSize);
	}

	public OrderPojo getCheck(Integer id) throws ApiException {
		OrderPojo order = orderDao.select(id);
		if (Objects.isNull(order))
			throw new ApiException("Order does not exist with id: " + id);
		return order;
	}

	public Integer getRecordsCount() {
		return orderDao.getRecordsCount();
	}




	@Autowired
	private OrderItemDao orderItemDao;

	public List<OrderItemPojo> addItem(List<OrderItemPojo> items) throws ApiException {
		for (OrderItemPojo item : items) {
			orderItemDao.insert(item);
		}
		return items;
	}

	public List<OrderItemPojo> getAllItems(Integer pageNo, Integer pageSize) {
		return orderItemDao.selectAll(pageNo, pageSize);
	}

	public OrderItemPojo getCheckItem(Integer id) throws ApiException {
		OrderItemPojo item = orderItemDao.select(id);
		if (Objects.isNull(item)) {
			throw new ApiException("Order with given ID does not exist, id: " + id);
		}
		return item;
	}

	public List<OrderItemPojo> getItemByOrderId(Integer id) throws ApiException {
		return orderItemDao.selectMultiple("orderId", id);
	}

	public Integer getItemsRecordsCount() {
		return orderItemDao.getRecordsCount();
	}

	public <T> List<OrderItemPojo> getItemByColumn(String column, List<T> values){
		return orderItemDao.selectByColumn(column, values);
	}

	public <T> List<OrderItemPojo> getItemByMultipleColumns(List<String> columns, List<List<T>> values) throws ApiException{
		if(columns.size() != values.size()) {
			throw new ApiException("Column and value list size should be same");
		}
		return orderItemDao.selectByMultipleColumns(columns, values);
	}

}
