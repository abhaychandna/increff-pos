package com.increff.pos.util;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;

public class ValidateUtil {
    
    public static void validateBrand(BrandPojo p) throws ApiException {
		if(StringUtil.isEmpty(p.getBrand()))
			throw new ApiException("Brand cannot be empty");
		if(StringUtil.isEmpty(p.getCategory()))
			throw new ApiException("Category cannot be empty");
	}
}
