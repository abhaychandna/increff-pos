package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
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
		List<InventoryPojo> inventory = inventoryService.getAll();
		HashMap<Integer, Integer> brandCategoryIdToQuantity = getQuantitiesForBrandCategoryIds(inventory);
        return getInventoryReport(brandCategoryIdToQuantity);
	}

    private List<InventoryReportData> getInventoryReport(HashMap<Integer, Integer> brandCategoryIdToQuantity) throws ApiException {
        List<InventoryReportData> inventoryReportList = new ArrayList<InventoryReportData>();
		for (Integer brandCategoryId : brandCategoryIdToQuantity.keySet()) {
			BrandPojo brand = brandService.get(brandCategoryId); // TODO : Make in query and get all brands at once 
			InventoryReportData inventoryReportData = new InventoryReportData(brand.getBrand(), brand.getCategory(),
                     brandCategoryIdToQuantity.get(brandCategoryId));
            inventoryReportList.add(inventoryReportData);
        }
        return inventoryReportList;
    }

    private HashMap<Integer, Integer> getQuantitiesForBrandCategoryIds(List<InventoryPojo> inventory) throws ApiException {
        HashMap<Integer, Integer> brandCategoryIdToQuantity = new HashMap<>();
        for (InventoryPojo inv : inventory) {
			Integer brandCategoryId = productService.get(inv.getId()).getBrandCategory();
            Integer quantity =  brandCategoryIdToQuantity.get(brandCategoryId);
            if(Objects.isNull(quantity)) quantity = 0;
            brandCategoryIdToQuantity.put(brandCategoryId, quantity + inv.getQuantity());
		}
        return brandCategoryIdToQuantity;
    }

    public List<SalesReportData> salesReport(SalesReportForm form) throws ApiException {
        // DateFormat 2023-01-01T19:07:34.190912345+05:30[Asia/Calcutta]
        // TODO : Change date format
        ZonedDateTime startDate = form.getStartDate(), endDate = form.getEndDate();
        //startDate = ZonedDateTime.parse(strStartDate);
        //endDate = ZonedDateTime.parse(strEndDate);
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
