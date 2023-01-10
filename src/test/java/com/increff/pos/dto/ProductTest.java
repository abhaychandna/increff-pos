package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

        BrandForm bf = TestUtil.getBrandFormDto(brand,category);
        BrandPojo bp = brandDto.add(bf);


        ProductForm f = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        ProductPojo p = productDto.add(f);

        assertEquals(bp.getId(), p.getBrand_category());
        assertEquals(barcode, p.getBarcode());
        assertEquals(name, p.getName());
        assertEquals(mrp, p.getMrp(), tolerance);
    }

    @Test
    public void testAddBrandCategoryDoesNotExist() throws ApiException{
        String brand = "adidas";
        String category = "tshirts";
        String barcode = "abcdef12";
        String name = "polo";
        double mrp = 100.5;

        ProductForm f = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        
        boolean error = false;
        try{
            ProductPojo p = productDto.add(f);
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
        ProductForm f = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        boolean error = false;
        try{
            ProductPojo p1 = productDto.add(f);
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

        ProductPojo p = TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductData d = productDto.get(p.getId());
        
        assertEquals(p.getId(), d.getId());
        assertEquals(brand, d.getBrand());
        assertEquals(category, d.getCategory());
        assertEquals(p.getBarcode(), d.getBarcode());
        assertEquals(p.getName(), d.getName());
        assertEquals(p.getMrp(), d.getMrp(), tolerance);
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

        ProductPojo p = TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductData d = productDto.get(p.getId());
        
        assertEquals(p.getId(), d.getId());
        assertEquals(brand, d.getBrand());
        assertEquals(category, d.getCategory());
        assertEquals(p.getBarcode(), d.getBarcode());
        assertEquals(p.getName(), d.getName());
        assertEquals(p.getMrp(), d.getMrp(), tolerance);
        
        String newBrand = "nike";
        String newCategory = "shoes";
        String newBarcode = "abcdef13";
        String newName = "polo";
        double newMrp = 200.5;
        
        TestUtil.createBrand(newBrand, newCategory);
        ProductForm f = TestUtil.getProductFormDto(newBarcode,newBrand,newCategory,newName,newMrp);
        productDto.update(p.getId(), f);
        
        ProductData d1 = productDto.get(p.getId());
        
        assertEquals(p.getId(), d1.getId());
        assertEquals(newBrand, d1.getBrand());
        assertEquals(newCategory, d1.getCategory());
        assertEquals(newBarcode, d1.getBarcode());
        assertEquals(newName, d1.getName());
        assertEquals(newMrp, d1.getMrp(), tolerance);
    }
}
