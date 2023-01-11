package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class ProductTest extends AbstractUnitTest{
    
    @Autowired
    private ProductDto productDto;
    @Autowired
    private ProductDao productDao;
    @Autowired 
    private BrandDto brandDto;
    
    private double tolerance = 1e-6;
    


    @Test
    public void testAddProduct() throws ApiException{
        String brand = "adidas";
        String category = "tshirts";
        String barcode = "abcdef12";
        String name = "polo";
        double mrp = 100.5;
        
        //ProductPojo p = createProductWithBrand(barcode, brand, category, name, mrp)

        BrandForm brandForm = TestUtil.getBrandFormDto(brand,category);
        BrandData brandData = brandDto.add(brandForm);

        ProductForm productForm = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        ProductData productData = productDto.add(productForm);

        ProductPojo product = productDao.select(ProductPojo.class, productData.getId());
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(name, product.getName());
        assertEquals(mrp, product.getMrp(), tolerance);
    }

    @Test
    public void testAddBrandCategoryDoesNotExist() throws ApiException{
        String brand = "adidas";
        String category = "tshirts";
        String barcode = "abcdef12";
        String name = "polo";
        double mrp = 100.5;

        ProductForm productForm = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        
        boolean error = false;
        try{
            productDto.add(productForm);
        }
        catch(ApiException e){
            error = true;
        }
        assertTrue(error);
    }

    @Test
    public void testDuplicateBarcode() throws ApiException{
        String brand = "adidas";
        String category = "tshirts";
        String barcode = "abcdef12";
        String name = "polo";
        double mrp = 100.5;

        TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductForm productForm = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        boolean error = false;
        try{
            productDto.add(productForm);
        }
        catch(ApiException e){
            error = true;
        }
        assertTrue(error);
    }
    
    @Test
    public void testGet() throws ApiException {
        String brand = "adidas";
        String category = "tshirts";
        String barcode = "abcdef12";
        String name = "polo";
        double mrp = 100.5;

        ProductData productData = TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductPojo product = productDao.select(ProductPojo.class, productData.getId());
        
        assertEquals(product.getId(), productData.getId());
        assertEquals(brand, productData.getBrand());
        assertEquals(category, productData.getCategory());
        assertEquals(product.getBarcode(), productData.getBarcode());
        assertEquals(product.getName(), productData.getName());
        assertEquals(product.getMrp(), productData.getMrp(), tolerance);
    }

    @Test
    public void testGetAll() throws ApiException {
        String brand = "adidas";
        String category = "tshirts";
        String barcode = "abcdef12";
        String name = "polo";
        double mrp = 100.5;

        TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        
        TestUtil.createProduct("abcdef13", brand, category, name, mrp);
        List<ProductData> d = productDto.getAll();
        assertEquals(d.size(), 2);
    }

    @Test
    public void testUpdate() throws ApiException {
        String brand = "adidas";
        String category = "tshirts";
        String barcode = "abcdef12";
        String name = "polo";
        double mrp = 100.5;

        ProductData productData = TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductPojo product = productDao.select(ProductPojo.class, productData.getId());
        assertEquals(product.getId(), productData.getId());
        assertEquals(brand, productData.getBrand());
        assertEquals(category, productData.getCategory());
        assertEquals(product.getBarcode(), productData.getBarcode());
        assertEquals(product.getName(), productData.getName());
        assertEquals(product.getMrp(), productData.getMrp(), tolerance);
        
        String newBrand = "nike";
        String newCategory = "shoes";
        String newBarcode = "abcdef13";
        String newName = "polo";
        double newMrp = 200.5;
        
        TestUtil.createBrand(newBrand, newCategory);
        ProductForm f = TestUtil.getProductFormDto(newBarcode,newBrand,newCategory,newName,newMrp);
        productDto.update(productData.getId(), f);
        
        productData = productDto.get(product.getId());
        
        assertEquals(product.getId(), productData.getId());
        assertEquals(newBrand, productData.getBrand());
        assertEquals(newCategory, productData.getCategory());
        assertEquals(newBarcode, productData.getBarcode());
        assertEquals(newName, productData.getName());
        assertEquals(newMrp, productData.getMrp(), tolerance);
    }
}
