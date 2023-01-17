package com.increff.pos.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;

public class PreProcessingUtil {

	private static String errorMessageSeparator = ".\n";
	public static <T> void normalizeAndValidate(T form) throws ApiException {
		normalize(form);
		validate(form);
	}

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
    
	public static <T> void normalize(T form) throws ApiException{
		for(java.lang.reflect.Field f : form.getClass().getDeclaredFields()) {
			try {
				if(f.getType().equals(String.class)) {
					f.setAccessible(true);
					f.set(form, StringUtil.toLowerCase(f.get(form).toString()).trim());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ApiException(e.getMessage());
			}
		}
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
