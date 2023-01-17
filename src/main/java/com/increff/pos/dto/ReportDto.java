package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;


@Component
public class ReportDto {

    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;


    public List<InventoryReportData> inventoryReport() throws ApiException {
        // TODO : Change pageNo and pageSize
		Integer pageNo = 0, pageSize = 1000;
		List<InventoryPojo> inventory = inventoryService.getAll(pageNo, pageSize);
		List<InventoryReportData> inventoryReportDatas = new ArrayList<InventoryReportData>();

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
			InventoryReportData inventoryReportData = new InventoryReportData();
            inventoryReportData.setBrand(brand.getBrand());
			inventoryReportData.setCategory(brand.getCategory());
			inventoryReportData.setQuantity(quantityMap.get(key));
			// print all 
			System.out.println(inventoryReportData.getBrand() + " " + inventoryReportData.getCategory() + " " + inventoryReportData.getQuantity());
            inventoryReportDatas.add(inventoryReportData);
        }

		return inventoryReportDatas;
	}

    public List<SalesReportData> salesReport(String strStartDate, String strEndDate, String brand, String category) throws ApiException {
        // DateFormat 2023-01-01T19:07:34.190912345+05:30[Asia/Calcutta]
        // TODO : Change date format
        // QUES : Best way to input Dates ? Unix Timestamp ? YYYYY-MM-DD ? 
        ZonedDateTime startDate = null, endDate = null;
        startDate = ZonedDateTime.parse(strStartDate);
        endDate = ZonedDateTime.parse(strEndDate);
        List<OrderPojo> orders = orderService.filterByDate(startDate, endDate);
        
        HashMap<Integer, List<String>> brandCategoryMap = new HashMap<>();
        HashMap<Integer, Integer> quantityMap = new HashMap<>();
        HashMap<Integer, Double> revenueMap = new HashMap<>();

        for (OrderPojo order:  orders){
            Integer orderId = order.getId();
            List<OrderItemPojo> orderItems = orderItemService.getByOrderId(orderId);
            for (OrderItemPojo orderItem: orderItems){
                Integer productId = orderItem.getProductId();
                if(!brandCategoryMap.containsKey(productId)){
                    Integer brandCategoryId = productService.get(productId).getBrandCategory();
                    BrandPojo brandPojo = brandService.get(brandCategoryId);
                    brandCategoryMap.put(productId, List.of(brandPojo.getBrand(), brandPojo.getCategory()));
                }
                // TODO : Add brand and category filters
                Integer quantity = orderItem.getQuantity();
                Double revenue = orderItem.getSellingPrice();
                if(quantityMap.containsKey(productId)){
                    quantity += 1;
                    revenue += revenueMap.get(productId);
                }
                quantityMap.put(productId, quantity);
                revenueMap.put(productId, revenue);
            }
        }

        List<SalesReportData> salesReportDatas = new ArrayList<SalesReportData>();
        for(Integer productId : quantityMap.keySet()){
            List<String> brandCategory = brandCategoryMap.get(productId);
            SalesReportData salesReportData = new SalesReportData();
            salesReportData.setBrand(brandCategory.get(0));
            salesReportData.setCategory(brandCategory.get(1));
            salesReportData.setQuantity(quantityMap.get(productId));
            salesReportData.setRevenue(revenueMap.get(productId));
            // print all 
            System.out.println(salesReportData.getBrand() + " " + salesReportData.getCategory() + " " + salesReportData.getQuantity() + " " + salesReportData.getRevenue());
            salesReportDatas.add(salesReportData);
        }
        return salesReportDatas;
    }


}