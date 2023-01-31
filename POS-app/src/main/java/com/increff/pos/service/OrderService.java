package com.increff.pos.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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
	private InventoryService inventoryService;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private DaySalesService daySalesService;

	public List<OrderItemPojo> add(List<OrderItemPojo> items) throws ApiException {
		if(items.isEmpty()) throw new ApiException("No items in order");		
		validateOrderQuantityInInventory(items);

		OrderPojo order = new OrderPojo();
		order.setTime(ZonedDateTime.now());
		dao.insert(order);

		for (OrderItemPojo item : items) {
			item.setOrderId(order.getId());
		}
		orderItemService.add(items);
		
		daySalesService.update(items);
		return items;
	}

	private void validateOrderQuantityInInventory(List<OrderItemPojo> items) throws ApiException {
		List<String> errorMessages = new ArrayList<String>();
		for(OrderItemPojo item : items){
			try{
				inventoryService.reduceInventory(item.getProductId(), item.getQuantity());
			}
			catch (ApiException e){
				errorMessages.add(e.getMessage());
			}
		}
		if(!errorMessages.isEmpty()){
			throw new ApiException(String.join("\n", errorMessages));
		} 
	}

	public OrderPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	public List<OrderPojo> filterByDate(ZonedDateTime start, ZonedDateTime end) {
		return dao.filterByDate(start, end);
	}

	public List<OrderPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, OrderPojo.class);
	}

	private OrderPojo getCheck(Integer id) throws ApiException {
		OrderPojo order = dao.select(OrderPojo.class, id);
		if (Objects.isNull(order))
			throw new ApiException("Order does not exist with id: " + id);
		return order;
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount(OrderPojo.class);
	}

	public <T> List<OrderPojo> getByColumn(String column, List<T> values){
		return dao.selectByColumn(OrderPojo.class, column, values);
	}
}
