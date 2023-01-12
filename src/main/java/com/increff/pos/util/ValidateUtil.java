package com.increff.pos.util;

import javax.transaction.Transactional;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

public class ValidateUtil {
    
    public static void validateBrand(BrandPojo p) throws ApiException {
		if(StringUtil.isEmpty(p.getBrand()))
			throw new ApiException("Brand cannot be empty");
		if(StringUtil.isEmpty(p.getCategory()))
			throw new ApiException("Category cannot be empty");
	}

	public static void validateProduct(ProductPojo p) throws ApiException{
		
		if(StringUtil.isEmpty(p.getBarcode()))
			throw new ApiException("Barcode cannot be empty");
		if(StringUtil.isEmpty(p.getName()))
			throw new ApiException("Name cannot be empty");

		if(p.getMrp()<=0)
			throw new ApiException("MRP should be positive");	
		
	}

	public static void validateInventory(InventoryPojo p) throws ApiException {
		if(p.getQuantity()<0)
			throw new ApiException("Quantity should be positive");
	}

	public static void validateOrderItem(OrderItemPojo p) throws ApiException {
		if(p.getQuantity()<=0)
			throw new ApiException("Quantity should be positive");
		if(p.getSellingPrice()<0)
			throw new ApiException("Selling Price cannot be negative");
	}
	public static void validateOrderItemPut(OrderItemPojo p) throws ApiException {
		if(p.getQuantity()<0)
			throw new ApiException("Quantity cannot be negative");
		if(p.getSellingPrice()<0)
			throw new ApiException("Selling Price cannot be negative");
	}
	public static void validateOrder(OrderPojo p) throws ApiException {
	}
}
