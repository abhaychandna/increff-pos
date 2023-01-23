package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.TimeUtil;


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
		HashMap<Integer, Integer> brandCategoryIdToQuantity = getBrandIdToQuantityMap(inventory);
        return getInventoryReport(brandCategoryIdToQuantity);
	}

    private List<InventoryReportData> getInventoryReport(HashMap<Integer, Integer> brandCategoryIdToQuantity) throws ApiException {
        List<Integer> brandCategoryIdList = brandCategoryIdToQuantity.keySet().stream().collect(Collectors.toList());
        HashMap<Integer, BrandPojo> brandIdToBrandPojo = getBrandIdToBrandPojoMap(brandCategoryIdList);

        List<InventoryReportData> inventoryReportList = new ArrayList<InventoryReportData>();   
        for (Integer brandCategoryId : brandCategoryIdToQuantity.keySet()) {
			BrandPojo brand = brandIdToBrandPojo.get(brandCategoryId);
			InventoryReportData inventoryReportData = new InventoryReportData(brand.getBrand(), brand.getCategory(),
                     brandCategoryIdToQuantity.get(brandCategoryId));
            inventoryReportList.add(inventoryReportData);
        }
        return inventoryReportList;
    }

    private HashMap<Integer, BrandPojo> getBrandIdToBrandPojoMap(List<Integer> brandCategoryIdList) {
        List<BrandPojo> brands = brandService.getByColumn("id", brandCategoryIdList);
        HashMap<Integer, BrandPojo> brandIdToBrandPojo = new HashMap<>();
		for(BrandPojo brand:brands) brandIdToBrandPojo.put(brand.getId(), brand);
        return brandIdToBrandPojo;
    }

    private HashMap<Integer, Integer> getBrandIdToQuantityMap(List<InventoryPojo> inventory) throws ApiException {
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
        ZonedDateTime startDate = TimeUtil.isoTimeStringToZonedDateTime(form.getStartDate());
        ZonedDateTime endDate = TimeUtil.isoTimeStringToZonedDateTime(form.getEndDate());
        
        List<BrandPojo> brands = getBrandPojoList(form.getBrand(), form.getCategory());
        System.out.println("brands : " + brands.size());
        if(brands.isEmpty()) return Collections.emptyList();
        List<ProductPojo> products = getProducts(brands);
        System.out.println("products : " + products.size());
        if(products.isEmpty()) return Collections.emptyList();

        List<Integer> productIds = products.stream().map(ProductPojo::getId).collect(Collectors.toList());
        List<OrderItemPojo> orderItems = getOrderItems(startDate, endDate, productIds);
        System.out.println("orderItems : " + orderItems.size());

        if(orderItems.isEmpty()) return Collections.emptyList(); 
        HashMap<Integer, List<String>> productIdToBrandCategory = getProductIdToBrandCategoryMap(products, brands);
        System.out.println("productIdToBrandCategory : " + productIdToBrandCategory.size());
        
        List<SalesReportData> salesReport = getSalesReport(orderItems, productIdToBrandCategory);
        
        String outputFilename = salesReportOutputFilename(startDate, endDate, form.getBrand(), form.getCategory());
        HashMap<String, String> headers = salesReportHeaders(startDate, endDate, form.getBrand(), form.getCategory());
        reportDataToPDF(salesReport, "salesReport", outputFilename, headers);

        return salesReport;
    }

    private HashMap<String, String> salesReportHeaders(ZonedDateTime startDate, ZonedDateTime endDate, String brand, String category) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("startDate", startDate.toString());
        headers.put("endDate", endDate.toString());
        if(Objects.nonNull(brand)) headers.put("brand", brand);
        if(Objects.nonNull(category)) headers.put("category", category);
        return headers;
    }

    private String salesReportOutputFilename(ZonedDateTime startDate, ZonedDateTime endDate,
            String brand, String category) throws ApiException {
        return "salesReport_" + startDate + "_" + endDate + "_" + "brand-"+ Objects.toString(brand, "") + "_" + "category-" + Objects.toString(category, "");
    }

    // Hashmap format : Key-productId, Value-List(brand, category)
    private HashMap<Integer, List<String>> getProductIdToBrandCategoryMap(List<ProductPojo> products,
            List<BrandPojo> brands) {
        HashMap<Integer, BrandPojo> brandIdToBrandPojo = new HashMap<>();
        brands.forEach(brand->{brandIdToBrandPojo.put(brand.getId(), brand);});
        HashMap<Integer, List<String>> productIdToBrandCategory = new HashMap<>();
        products.forEach(product->{
            BrandPojo brand = brandIdToBrandPojo.get(product.getBrandCategory());
            productIdToBrandCategory.put(product.getId(), Arrays.asList(brand.getBrand(), brand.getCategory()));
        });
        return productIdToBrandCategory;
    }

    private List<ProductPojo> getProducts(List<BrandPojo> brands) {
        List<Integer> brandIds = brands.stream().map(BrandPojo::getId).collect(Collectors.toList());
        return productService.getByColumn("brandCategory", brandIds);
    }

    private List<Integer> getOrderIds(ZonedDateTime startDate, ZonedDateTime endDate) {
        return orderService.filterByDate(startDate, endDate).stream().map(OrderPojo::getId).collect(Collectors.toList());
    }

    private List<OrderItemPojo> getOrderItems(ZonedDateTime startDate, ZonedDateTime endDate, List<Integer> productIds){
        List<Integer> orderIds = getOrderIds(startDate, endDate);
        System.out.println("orderIds : " + orderIds.size());
        List<String> columns = Arrays.asList("orderId", "productId");
        List<List<Object>> values = Arrays.asList(
            orderIds.stream().map(e->(Object)e).collect(Collectors.toList()),
            productIds.stream().map(e->(Object)e).collect(Collectors.toList())
        );
        List<OrderItemPojo> orderItems = orderItemService.getByMultipleColumns(columns, values);
        
        // Converting to Set for faster lookup in filter orderItems
        Set<Integer> productIdSet = productIds.stream().collect(Collectors.toSet());
        orderItems = orderItems.stream().filter(e->productIdSet.contains(e.getProductId())).collect(Collectors.toList());
        return orderItems;
    }

    private List<SalesReportData> getSalesReport(List<OrderItemPojo> orderItems, HashMap<Integer, List<String>> productIdToBrandCategory) {
        // print all inputs 
        System.out.println("orderItems : " + orderItems.size());
        System.out.println("productIdToBrandCategory : " + productIdToBrandCategory.size());
        productIdToBrandCategory.forEach((k,v)->{
            System.out.println("k : " + k + " v : " + v);
        });
        HashMap<Integer, SalesReportData> productIdToSalesReportData = new HashMap<Integer, SalesReportData>();
        orderItems.forEach(item->{
            Integer productId = item.getProductId();
            List<String> brandCategory = productIdToBrandCategory.get(productId);
            String brand = brandCategory.get(0), category = brandCategory.get(1);
            SalesReportData salesReportData = productIdToSalesReportData.getOrDefault(productId,
                    new SalesReportData(brand, category, 0 , 0.0));

            salesReportData.setQuantity(salesReportData.getQuantity() + item.getQuantity());
            salesReportData.setRevenue(salesReportData.getRevenue() + (item.getSellingPrice() * item.getQuantity()));
            productIdToSalesReportData.put(productId, salesReportData);
        });
        return new ArrayList<SalesReportData>(productIdToSalesReportData.values());
    }

    private List<BrandPojo> getBrandPojoList(String brand, String category) throws ApiException {
        System.out.println("brand : " + brand + " category : " + category);
        if (Objects.isNull(brand) && Objects.isNull(category)) return brandService.getAll();
        if(Objects.nonNull(category) && Objects.nonNull(brand)) {
            List<BrandPojo> brands = brandService.getByMultipleColumns(Arrays.asList("category", "brand"), Arrays.asList(
                Arrays.asList(category),
                Arrays.asList(brand))
            );
            return brands;
        }
        if(Objects.nonNull(category)) return brandService.getByColumn("category", Arrays.asList(category));
        if(Objects.nonNull(brand)) return brandService.getByColumn("brand", Arrays.asList(brand));

        return Collections.emptyList();
    }

}
