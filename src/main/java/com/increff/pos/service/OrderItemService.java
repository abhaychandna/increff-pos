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
	@Autowired
	private InventoryService inventoryService;

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

	public void update(Integer id, OrderItemPojo item) throws ApiException {
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
	
	public Integer getRecordsCount() {
		return dao.getRecordsCount(OrderItemPojo.class);
	}
}
