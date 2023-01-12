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

	public InventoryPojo getCheck(Integer id) throws ApiException {
		InventoryPojo inventory = dao.select(InventoryPojo.class, id);
		if (Objects.isNull(inventory)) {
			throw new ApiException("Inventory with given ID does not exist, id: " + id);
		}
		return inventory;
	}
}
