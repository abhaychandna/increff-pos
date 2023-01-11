package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.InventoryPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InventoryService {

	@Autowired
	private InventoryDao dao;
	
	public InventoryPojo add(InventoryPojo p) throws ApiException {
				
		dao.insert(p);
		return p;
	}

	public InventoryPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	public List<InventoryPojo> getAll() {
		return dao.selectAll();
	}

	public void update(int id, InventoryPojo p) throws ApiException {
		InventoryPojo ex = getCheck(id);

		ex.setQuantity(p.getQuantity());
		dao.update(ex);
	}

	public InventoryPojo getCheck(int id) throws ApiException {
		InventoryPojo p = dao.select(InventoryPojo.class, id);
		if (Objects.isNull(p)) {
			throw new ApiException("Inventory with given ID does not exist, id: " + id);
		}
		return p;
	}
}
