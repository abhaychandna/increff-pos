package com.increff.pos.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderItemService {

	@Autowired
	private OrderItemDao dao;
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private DaySalesService daySalesService;

	public List<OrderItemPojo> add(List<OrderItemPojo> items) throws ApiException {

		inventoryService.reduceInventory(items);

		// creating order
		OrderPojo order = new OrderPojo();
		order.setTime(new Date());
		order = orderService.add(order);

		// creating order item
		for (OrderItemPojo item : items) {
			item.setOrderId(order.getId());
			dao.insert(item);
		}
		daySalesService.update(items);
		return items;
	}




	public OrderItemPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	public List<OrderItemPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, OrderItemPojo.class);
	}

	public void update(Integer id, OrderItemPojo item) throws ApiException {
		System.out.println("service.update");
		OrderItemPojo existing = getCheck(id);

		Integer extra = item.getQuantity() - existing.getQuantity();
		if(extra > 0){
			inventoryService.reduceInventory(existing.getProductId(), extra);
		}
		else if(extra < 0){
			inventoryService.increaseInventory(existing.getProductId(), -extra);
		}
		else{
			return;
		}
		existing.setQuantity(item.getQuantity());
		existing.setSellingPrice(item.getSellingPrice());
		dao.update(existing);
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
}
