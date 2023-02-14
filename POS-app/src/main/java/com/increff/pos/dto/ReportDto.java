package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.data.XSLTFilename;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ClientWrapper;
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
    @Autowired
    private ClientWrapper ClientWrapper;

    public String inventoryReport() throws ApiException{
		List<InventoryPojo> inventory = inventoryService.getAll();
        if(inventory.isEmpty()) throw new ApiException("No inventory found");
		HashMap<Integer, Integer> brandCategoryIdToQuantity = getBrandIdToQuantityMap(inventory);
        List<InventoryReportData> reportData = getInventoryReport(brandCategoryIdToQuantity);
        String base64  = ClientWrapper.getPdfClient().getPDFInBase64(reportData, XSLTFilename.INVENTORY_REPORT, null);
        return base64;
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

    private HashMap<Integer, Integer> getBrandIdToQuantityMap(List<InventoryPojo> inventory) throws ApiException{
        HashMap<Integer, Integer> brandCategoryIdToQuantity = new HashMap<>();
        for (InventoryPojo inv : inventory) {
			Integer brandCategoryId = productService.getCheck(inv.getProductId()).getBrandCategory();
            Integer quantity =  brandCategoryIdToQuantity.get(brandCategoryId);
            if(Objects.isNull(quantity)) quantity = 0;
            brandCategoryIdToQuantity.put(brandCategoryId, quantity + inv.getQuantity());
		}
        return brandCategoryIdToQuantity;
    }




    public String salesReport(SalesReportForm form) throws ApiException {
        ZonedDateTime startDate = TimeUtil.isoTimeStringToZonedDateTime(form.getStartDate());
        ZonedDateTime endDate = TimeUtil.isoTimeStringToZonedDateTime(form.getEndDate());
        if(endDate.isBefore(startDate)) throw new ApiException("Start date cannot be greater than end date");
        List<BrandPojo> brands = getBrandPojoList(form.getBrand(), form.getCategory());

        List<ProductPojo> products = getProducts(brands);

        List<Integer> productIds = products.stream().map(ProductPojo::getId).collect(Collectors.toList());
        List<OrderItemPojo> orderItems = getOrderItems(startDate, endDate, productIds);

        if(orderItems.isEmpty()) throw new ApiException("No orders found for the given criteria"); 
        
        HashMap<Integer, List<String>> productIdToBrandCategory = getProductIdToBrandCategoryMap(products, brands);
        
        List<SalesReportData> salesReport = getSalesReport(orderItems, productIdToBrandCategory);
        
        HashMap<String, String> headers = salesReportHeaders(startDate, endDate, form.getBrand(), form.getCategory());
        String base64 = ClientWrapper.getPdfClient().getPDFInBase64(salesReport, XSLTFilename.SALES_REPORT, headers);

        return base64;
    }

    private HashMap<String, String> salesReportHeaders(ZonedDateTime startDate, ZonedDateTime endDate, String brand, String category) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("startDate", TimeUtil.getFormattedTime(startDate, "dd-MM-yyyy HH:mm:ss z"));
        headers.put("endDate", TimeUtil.getFormattedTime(endDate, "dd-MM-yyyy HH:mm:ss z"));
        headers.put("brand", Objects.nonNull(brand) ? brand : "All Brands");
        headers.put("category", Objects.nonNull(category) ? category : "All Categories");
        return headers;
    }

    // Return Hashmap format : Key-productId, Value-List(brand, category)
    private HashMap<Integer, List<String>> getProductIdToBrandCategoryMap(List<ProductPojo> products,
            List<BrandPojo> brands) {
        HashMap<Integer, BrandPojo> brandIdToBrandPojo = new HashMap<>();
        brands.stream().forEach(brand->{brandIdToBrandPojo.put(brand.getId(), brand);});
        HashMap<Integer, List<String>> productIdToBrandCategory = new HashMap<>();
        products.stream().forEach(product->{
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

    private List<OrderItemPojo> getOrderItems(ZonedDateTime startDate, ZonedDateTime endDate, List<Integer> productIds) throws ApiException{
        List<Integer> orderIds = getOrderIds(startDate, endDate);
        List<String> columns = Arrays.asList("orderId", "productId");
        List<List<Object>> values = Arrays.asList(
            orderIds.stream().map(e->(Object)e).collect(Collectors.toList()),
            productIds.stream().map(e->(Object)e).collect(Collectors.toList())
        );
        List<OrderItemPojo> orderItems = orderItemService.getByMultipleColumns(columns, values);
        return orderItems;
    }

    private List<SalesReportData> getSalesReport(List<OrderItemPojo> orderItems, HashMap<Integer, List<String>> productIdToBrandCategory) {
        HashMap<Integer, SalesReportData> productIdToSalesReportData = new HashMap<Integer, SalesReportData>();
        orderItems.stream().forEach(item->{
            Integer productId = item.getProductId();
            List<String> brandCategory = productIdToBrandCategory.get(productId);
            String brand = brandCategory.get(0), category = brandCategory.get(1);
            SalesReportData salesReportData = productIdToSalesReportData.getOrDefault(productId,
                    new SalesReportData(brand, category, 0 , 0.0));

            salesReportData.setQuantity(salesReportData.getQuantity() + item.getQuantity());
            salesReportData.setRevenue(salesReportData.getRevenue() + (item.getSellingPrice() * item.getQuantity()));
            salesReportData.setRevenue(Double.valueOf(String.format("%.2f", salesReportData.getRevenue())));
            productIdToSalesReportData.put(productId, salesReportData);
        });
        return new ArrayList<SalesReportData>(productIdToSalesReportData.values());
    }

    private List<BrandPojo> getBrandPojoList(String brand, String category) throws ApiException {
        if(Objects.nonNull(category) && Objects.nonNull(brand)) {
            List<BrandPojo> brands = brandService.getByMultipleColumns(Arrays.asList("category", "brand"), Arrays.asList(
                Arrays.asList(category),
                Arrays.asList(brand))
            );
            return brands;
        }
        if(Objects.nonNull(category)) return brandService.getByColumn("category", Arrays.asList(category));
        if(Objects.nonNull(brand)) return brandService.getByColumn("brand", Arrays.asList(brand));
        return brandService.getAll();
    }




    public String brandReport() throws ApiException{
        List<BrandPojo> brands = brandService.getAll();
        if(brands.isEmpty()) throw new ApiException("No brands found");
        List<BrandReportData> reportDataList = new ArrayList<>();
        brands.stream().forEach(brand->{
            reportDataList.add(new BrandReportData(brand.getBrand(), brand.getCategory()));
        });

        String base64  = ClientWrapper.getPdfClient().getPDFInBase64(reportDataList, XSLTFilename.BRAND_REPORT, null);
        return base64;

    }

}
