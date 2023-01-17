package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.SalesReportData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.ReportService;


@Component
public class ReportDto {
    
    @Autowired
    ReportService svc;

    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;


    public List<SalesReportData> inventoryReport() throws ApiException {
        // TODO : Change pageNo and pageSize
		Integer pageNo = 0, pageSize = 1000;
		List<InventoryPojo> inventory = inventoryService.getAll(pageNo, pageSize);
		List<SalesReportData> inventoryReportDatas = new ArrayList<SalesReportData>();

		HashMap<Integer, Integer> quantityMap = new HashMap<>(); 
		for (InventoryPojo inv : inventory) {
			Integer brandCategoryId = productService.get(inv.getId()).getBrandCategory();
            Integer quantity =  quantityMap.get(brandCategoryId);
            if(quantity == null) quantity = 0;
            quantityMap.put(brandCategoryId, quantity + inv.getQuantity());
		}
		System.out.println(quantityMap);
		
		for (Integer key : quantityMap.keySet()) {
			BrandPojo brand = brandService.get(key);
			SalesReportData inventoryReportData = new SalesReportData();
            inventoryReportData.setBrand(brand.getBrand());
			inventoryReportData.setCategory(brand.getCategory());
			inventoryReportData.setQuantity(quantityMap.get(key));
			// print all 
			System.out.println(inventoryReportData.getBrand() + " " + inventoryReportData.getCategory() + " " + inventoryReportData.getQuantity());
            inventoryReportDatas.add(inventoryReportData);
        }

		return inventoryReportDatas;
	}




}
