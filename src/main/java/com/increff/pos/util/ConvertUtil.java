package com.increff.pos.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemPutForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;

@Component
public class ConvertUtil {

	private static BrandService bService;
	private static ProductService productService;

	@Autowired
	BrandService brandService;
	@Autowired
	ProductService pService;

	@PostConstruct
	private void init() {
		bService = this.brandService;
		productService = this.pService;
	}

	// QUES : Where to add database calls if needed? (Eg. converting brand category
	// string to BC_ID in conversion)
	// Do we override the convert method for each class?
	public static <T, R> R convert(T fromClass, Class<R> toClass) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		R newObject = mapper.convertValue(fromClass, toClass);
		return newObject;
	}

    public static BrandData convert(BrandPojo p) {
		return convert(p, BrandData.class);
	}
	
	public static BrandPojo convert(BrandForm f) {
		return convert(f, BrandPojo.class);
	}

	public static ProductPojo convert(ProductForm f) throws ApiException {
		ProductPojo p = new ProductPojo();
		p.setBarcode(f.getBarcode());
        BrandPojo brand = bService.getCheckBrandCategory(f.getBrand(), f.getCategory());
		p.setBrandCategory(brand.getId());
		p.setMrp(f.getMrp());
		p.setName(f.getName());
		return p;
	}

	public static ProductData convert(ProductPojo p) throws ApiException{
		ProductData f = new ProductData();
		f.setBarcode(p.getBarcode());
        BrandPojo brand = bService.get(p.getBrandCategory());
		f.setBrand(brand.getBrand());
        f.setCategory(brand.getCategory());
		f.setMrp(p.getMrp());
		f.setName(p.getName());
		f.setId(p.getId());
		return f;
	}

	public static InventoryPojo convert(InventoryForm f) throws ApiException {
		InventoryPojo p = new InventoryPojo();
		p.setId(productService.getByBarcode(f.getBarcode()).getId());
		p.setQuantity(f.getQuantity());
		return p;
	}
	public static InventoryData convert(InventoryPojo p) throws ApiException{
		InventoryData d = new InventoryData();
		d.setBarcode(productService.get(p.getId()).getBarcode());
		d.setQuantity(p.getQuantity());
		d.setId(p.getId());
		return d;
	}

	public static OrderItemPojo convert(OrderItemForm f) throws ApiException{
		OrderItemPojo p = new OrderItemPojo();
		p.setProductId(productService.getByBarcode(f.getBarcode()).getId());
		p.setSellingPrice(f.getSellingPrice());
		p.setQuantity(f.getQuantity());
		return p;
	}

	public static OrderItemData convert(OrderItemPojo p) throws ApiException {
		OrderItemData d = new OrderItemData();
		d.setBarcode(productService.get(p.getProductId()).getBarcode());
		d.setId( p.getId());
		d.setOrderId(p.getOrderId());
		d.setQuantity(p.getQuantity());
		d.setSellingPrice(p.getSellingPrice());
		return d;
	}
}
