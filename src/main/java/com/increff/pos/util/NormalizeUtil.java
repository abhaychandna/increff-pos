package com.increff.pos.util;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
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
	
	public static void normalize(InventoryPojo p) {
	}

	public static void normalize(OrderItemPojo p){
	}

	public static void normalize(OrderPojo p){
	}
}
