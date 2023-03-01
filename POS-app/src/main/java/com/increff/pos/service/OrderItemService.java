package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.increff.pos.util.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderItemService {

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
