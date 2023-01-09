package com.increff.pos.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;

@Component
public class ConvertUtil {

	private static BrandService bService;
	@Autowired
	BrandService brandService;

	@PostConstruct
	private void init(){
		bService = this.brandService;
	}

    public static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setBrand(p.getBrand());
		d.setCategory(p.getCategory());
		d.setId(p.getId());
		return d;
	}
	
	public static BrandPojo convert(BrandForm f) {
		BrandPojo p = new BrandPojo();
		p.setBrand(f.getBrand());
		p.setCategory(f.getCategory());
		return p;
	}


	public static ProductPojo convert(ProductForm f) throws ApiException {
		ProductPojo p = new ProductPojo();
		p.setBarcode(f.getBarcode());
        BrandPojo brand = bService.getCheckBrandCategory(f.getBrand(), f.getCategory());
		p.setBrand_category(brand.getId());
		p.setMrp(f.getMrp());
		p.setName(f.getName());
		return p;
	}

	public static ProductData convert(ProductPojo p) throws ApiException{
		ProductData f = new ProductData();
		f.setBarcode(p.getBarcode());
        BrandPojo brand = bService.get(p.getBrand_category());
		f.setBrand(brand.getBrand());
        f.setCategory(brand.getCategory());
		f.setMrp(p.getMrp());
		f.setName(p.getName());
		f.setId(p.getId());
		return f;
	}
}
