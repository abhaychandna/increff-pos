package com.increff.pos.util;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;

public class NormalizeUtil {
    public static void normalize(BrandPojo p) {
		p.setBrand(StringUtil.toLowerCase(p.getBrand()).trim());
		p.setCategory(StringUtil.toLowerCase(p.getCategory()).trim());
	}

	public static void normalize(ProductPojo p){
		p.setName(p.getName().toLowerCase().trim());
		p.setBarcode(p.getBarcode().toLowerCase().trim());
	}
}
