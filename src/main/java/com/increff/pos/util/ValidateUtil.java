package com.increff.pos.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

public class ValidateUtil {

	private static String errorMessageSeparator = ".\n";
	public static <T> void validate(T form) throws ApiException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
		if(violations.size()>0){
			String error = "FORM ERRORS: ";
			for(ConstraintViolation<T> v : violations) {
				error += v.getMessage() + errorMessageSeparator;
			}
			throw new ApiException(error);
		}
	}
    
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
	public static void validateOrderItems(List<OrderItemPojo> items) throws ApiException {
		Set<Integer> productIds = new HashSet<Integer>();
		for(OrderItemPojo item : items) {
			if(productIds.contains(item.getProductId()))
				throw new ApiException("Duplicate product id: " + item.getProductId() + " in order");
			productIds.add(item.getProductId());	
			validateOrderItem(item);
		}
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
