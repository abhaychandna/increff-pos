package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InventoryService {

	@Autowired
	private InventoryDao dao;
	
	public InventoryPojo add(InventoryPojo inventory) throws ApiException {
        InventoryPojo existing = dao.select(InventoryPojo.class, inventory.getId());
		if (Objects.nonNull(existing)) {
			throw new ApiException("Inventory already exists");
        }
		dao.insert(inventory);
		return inventory;
	}

	public InventoryPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	public List<InventoryPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, InventoryPojo.class);
	}

	public void update(Integer id, InventoryPojo inventory) throws ApiException {
		InventoryPojo existing = getCheck(id);

		existing.setQuantity(inventory.getQuantity());
		dao.update(existing);
	}

	public void reduceInventory(List<OrderItemPojo> items) throws ApiException {
		for (OrderItemPojo item : items) {
			reduceInventory(item.getProductId(), item.getQuantity());
		}
	}
	public void reduceInventory(Integer productId, Integer quantity) throws ApiException {
		InventoryPojo inventory = get(productId);
		if(inventory.getQuantity() < quantity) {
			throw new ApiException("Insufficient inventory for productId: " + productId + ". Available: " + inventory.getQuantity() + ", Required: " + quantity);
		}
		inventory.setQuantity(inventory.getQuantity() - quantity);
		dao.update(inventory);
	}
	public void increaseInventory(Integer productId, Integer quantity) throws ApiException {
		InventoryPojo inventory = get(productId);
		inventory.setQuantity(inventory.getQuantity() + quantity);
		dao.update(inventory);
	}



	public InventoryPojo getCheck(Integer id) throws ApiException {
		InventoryPojo inventory = dao.select(InventoryPojo.class, id);
		if (Objects.isNull(inventory)) {
			throw new ApiException("Inventory with given ID does not exist, id: " + id);
		}
		return inventory;
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount(InventoryPojo.class);
	}
}
