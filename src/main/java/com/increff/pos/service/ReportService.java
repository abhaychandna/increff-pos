package com.increff.pos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.InventoryReportData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ReportService {

	@Autowired
	private InventoryDao inventoryDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private BrandDao brandDao;
	
	public List<InventoryReportData> inventoryReport() throws ApiException {
		Integer pageNo = 0, pageSize = 1000;
		List<InventoryPojo> inventory = inventoryDao.selectAll(pageNo, pageSize, InventoryPojo.class);
		InventoryReportData inventoryReportData = new InventoryReportData();
		List<InventoryReportData> inventoryReportDatas = new ArrayList<InventoryReportData>();

		HashMap<Integer, Integer> map = new HashMap<>(); 
		for (InventoryPojo inv : inventory) {
			Integer brandCategoryId = productDao.select(ProductPojo.class, inv.getId()).getBrandCategory();
			if(map.containsKey(brandCategoryId)) {
				map.put(brandCategoryId, map.get(brandCategoryId) + inv.getQuantity());
			}
			else {
				map.put(brandCategoryId, inv.getQuantity());
			}
		}
		System.out.println(map);
		
		for (Integer key : map.keySet()) {
			BrandPojo brand = brandDao.select(BrandPojo.class, key);
			inventoryReportData.setBrand(brand.getBrand());
			inventoryReportData.setCategory(brand.getCategory());
			inventoryReportData.setQuantity(map.get(key));
			inventoryReportDatas.add(inventoryReportData);
			// print all 
			System.out.println(inventoryReportData.getBrand() + " " + inventoryReportData.getCategory() + " " + inventoryReportData.getQuantity());
		}

		return inventoryReportDatas;
	}
}
