package com.increff.pos.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.ConvertUtil;

@Component
public class TestUtil {

    private static BrandDto brandDto;
    private static ProductDto productDto;
    private static InventoryDto inventoryDto;
    private static BrandDao brandDao;
    @Autowired
    BrandDto bDto;
    @Autowired
    ProductDto pDto;
    @Autowired
    InventoryDto iDto;

    @Autowired
    BrandDao bDao;

	@PostConstruct
	private void init(){
        brandDto = this.bDto;
        productDto = this.pDto;
        inventoryDto = this.iDto;
        brandDao = this.bDao;
	}

    public static ProductData createProductWithBrand(String barcode, String brand, String category, String name, Double mrp) throws ApiException {
        createBrand(brand, category);
        ProductData p = createProduct(barcode, brand, category, name, mrp);
        return p;
    }

    public static BrandData createBrand(String brand, String category) throws ApiException{
        BrandForm bf = TestUtil.getBrandFormDto(brand,category);
        BrandPojo brandPojo = ConvertUtil.convert(bf);
        brandPojo = brandDao.insert(brandPojo);
        //brandPojo = brandDao.select(BrandPojo.class, brandPojo.getBrand());
        BrandData brandData = ConvertUtil.convert(brandPojo);
        return brandData;
    }

    public static ProductData createProduct(String barcode, String brand, String category, String name, Double mrp) throws ApiException {
        ProductForm productForm = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        ProductData productData = productDto.add(productForm);
        return productData;
    }

    public static InventoryData createInventory(String barcode, String brand, String category, String name, Double mrp, Integer quantity) throws ApiException {
        createProductWithBrand(barcode, brand, category, name, mrp);
        InventoryForm inventoryForm = TestUtil.getInventoryFormDto(barcode,quantity);
        InventoryData inventoryData = inventoryDto.add(inventoryForm);
        return inventoryData;
    }
    public static InventoryData createInventorySingle(String barcode, Integer quantity) throws ApiException {
        InventoryForm inventoryForm = TestUtil.getInventoryFormDto(barcode,quantity);
        InventoryData inventoryData = inventoryDto.add(inventoryForm);
        return inventoryData;
    }
    public static BrandForm getBrandFormDto(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static ProductForm getProductFormDto(String barcode, String brand, String category, String name, Double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setName(name);
        productForm.setMrp(mrp);
        return productForm;
    }

    public static InventoryForm getInventoryFormDto(String barcode, Integer quantity){
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }
}
