package com.increff.pos.util;

import javax.transaction.Transactional;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

public class ValidateUtil {
    
    public static void validateBrand(BrandPojo p) throws ApiException {
		if(StringUtil.isEmpty(p.getBrand()))
			throw new ApiException("Brand cannot be empty");
		if(StringUtil.isEmpty(p.getCategory()))
			throw new ApiException("Category cannot be empty");
	}

    //@Transactional
	public static void validateProduct(ProductPojo p) throws ApiException{
		
		if(StringUtil.isEmpty(p.getBarcode()))
			throw new ApiException("Barcode cannot be empty");
		if(StringUtil.isEmpty(p.getName()))
			throw new ApiException("Name cannot be empty");

		if(p.getMrp()<=0)
			throw new ApiException("MRP should be positive");	
		
		System.out.println("Brand Category " + p.getBrand_category());
	}
}
