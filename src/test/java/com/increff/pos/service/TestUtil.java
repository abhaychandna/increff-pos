package com.increff.pos.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;

@Component
public class TestUtil {

    private static BrandDto brandDto;
    private static ProductDto productDto;
    @Autowired
    BrandDto bDto;
    @Autowired
    ProductDto pDto;

	@PostConstruct
	private void init(){
        brandDto = this.bDto;
        productDto = this.pDto;
	}

    public static ProductPojo createProductWithBrand(String barcode, String brand, String category, String name, double mrp) throws ApiException {
        createBrand(brand, category);
        ProductPojo p = createProduct(barcode, brand, category, name, mrp);
        return p;
    }

    public static BrandPojo createBrand(String brand, String category) throws ApiException{
        BrandForm bf = TestUtil.getBrandFormDto(brand,category);
        BrandPojo bp = brandDto.add(bf);
        return bp;
    }

    public static ProductPojo createProduct(String barcode, String brand, String category, String name, double mrp) throws ApiException {
        ProductForm f = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        ProductPojo p = productDto.add(f);
        return p;
    }

    public static BrandForm getBrandFormDto(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static ProductForm getProductFormDto(String barcode, String brand, String category, String name, double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setName(name);
        productForm.setMrp(mrp);
        return productForm;
    }

    public static InventoryForm getInventoryFormDto(String barcode, int quantity){
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }
}
