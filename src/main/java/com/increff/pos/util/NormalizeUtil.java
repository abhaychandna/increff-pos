package com.increff.pos.util;

import com.increff.pos.pojo.BrandPojo;

public class NormalizeUtil {
    public static void normalize(BrandPojo p) {
		p.setBrand(StringUtil.toLowerCase(p.getBrand()).trim());
		p.setCategory(StringUtil.toLowerCase(p.getCategory()).trim());
	}
}
