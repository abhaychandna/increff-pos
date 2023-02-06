package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InventoryService {

	@Autowired
	private InventoryDao dao;
	
	public InventoryPojo add(InventoryPojo inventory) throws ApiException {
        InventoryPojo existing = dao.select(inventory.getProductId());
		if (Objects.nonNull(existing)) {
			existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
			return existing;
        }
		dao.insert(inventory);
		return inventory;
	}
	
	public void bulkAdd(List<InventoryPojo> inventories) throws ApiException {
		for(InventoryPojo inventory : inventories) {
			add(inventory);
		}
	}

	public List<InventoryPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize);
	}

	public List<InventoryPojo> getAll() {
		return dao.selectAll();
	}

	public void update(Integer id, Integer quantity) throws ApiException {
		InventoryPojo existing = getCheck(id);
		existing.setQuantity(quantity);
	}

	public void reduceInventory(Integer productId, Integer quantity) throws ApiException {
		InventoryPojo inventory = getCheck(productId);
		if(inventory.getQuantity() < quantity) {
			throw new ApiException("Insufficient inventory for productId: " + productId + ". Available: " + inventory.getQuantity() + ", Required: " + quantity);
		}
		inventory.setQuantity(inventory.getQuantity() - quantity);
	}

	public InventoryPojo getCheck(Integer id) throws ApiException {
		InventoryPojo inventory = dao.selectByColumn("productId", id);
		if (Objects.isNull(inventory)) {
			throw new ApiException("Inventory with given ID does not exist");
		}
		return inventory;
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount();
	}
}
