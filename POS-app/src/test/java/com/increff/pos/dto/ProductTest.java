package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.ProductPutForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class ProductTest extends AbstractUnitTest{
    
    @Autowired
    private ProductDto productDto;
    @Autowired
    private ProductDao productDao;
    
    private Double tolerance = 1e-6;
    
    private static String brand;
    private static String category;
    private static String barcode;
    private static String name;
    private static Double mrp;
    @BeforeClass
    public static void init() throws ApiException{
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.5;
    }

    @Test
    public void testAddProduct() throws ApiException{
        BrandData brandData = TestUtil.createBrand(brand, category);
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
        ProductForm productForm = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        try{
            productDto.add(productForm);
        }
        catch(ApiException e){
            return;
        }
        fail();
    }

    @Test
    public void testDuplicateBarcode() throws ApiException{
        TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductForm productForm = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        try{
            productDto.add(productForm);
        }
        catch(ApiException e){
            return;
        }
        fail();
    }
    
    @Test
    public void testGet() throws ApiException {
        ProductPojo product = TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductData productData = productDto.get(product.getId());
        
        assertEquals(product.getId(), productData.getId());
        assertEquals(brand, productData.getBrand());
        assertEquals(category, productData.getCategory());
        assertEquals(product.getBarcode(), productData.getBarcode());
        assertEquals(product.getName(), productData.getName());
        assertEquals(product.getMrp(), productData.getMrp(), tolerance);
    }

    @Test
    public void testGetAll() throws ApiException {
        TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        
        TestUtil.createProduct("abcdef13", brand, category, name, mrp);
        PaginatedData<ProductData> d = productDto.getAll(0,10, 1);
        assertEquals(d.getData().size(), 2);
    }

    @Test
    public void testUpdate() throws ApiException {
        ProductPojo product = TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductData productData = productDto.get(product.getId());
        
        String newName = "polo";
        Double newMrp = 200.5;
        
        ProductPutForm form = new ProductPutForm(newName, newMrp);
        productDto.update(productData.getId(), form);
        
        productData = productDto.get(product.getId());
        assertEquals(product.getId(), productData.getId());
        assertEquals(newName, productData.getName());
        assertEquals(newMrp, productData.getMrp(), tolerance);
    }

    @Test
    public void testGetByBarcode() throws ApiException {
        ProductPojo product = TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        ProductData productData = productDto.getByBarcode(product.getBarcode());
        
        assertEquals(product.getId(), productData.getId());
        assertEquals(brand, productData.getBrand());
        assertEquals(category, productData.getCategory());
        assertEquals(product.getBarcode(), productData.getBarcode());
        assertEquals(product.getName(), productData.getName());
        assertEquals(product.getMrp(), productData.getMrp(), tolerance);
    }


    @Test
    public void testBulkAdd() throws ApiException, JsonProcessingException{
        TestUtil.createBrand(brand, category);

        ProductForm productForm1 = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        ProductForm productForm2 = TestUtil.getProductFormDto("abcdef13",brand,category,name,mrp);
        ProductForm productForm3 = TestUtil.getProductFormDto("abcdef14",brand,category,name,mrp);
        List<ProductForm> productForms = List.of(productForm1, productForm2, productForm3);

        productDto.bulkAdd(productForms);
        List<ProductData> productDatas = productDto.getAll(0, 10, 1).getData();
        assertEquals(productDatas.size(), 3);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddExisting() throws ApiException, JsonProcessingException {
        TestUtil.createBrand(brand, category);

        ProductForm productForm1 = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        ProductForm productForm2 = TestUtil.getProductFormDto("abcdef13",brand,category,name,mrp);
        ProductForm productForm3 = TestUtil.getProductFormDto("abcdef14",brand,category,name,mrp);
        List<ProductForm> productForms = List.of(productForm1, productForm2, productForm3);

        productDto.bulkAdd(productForms);
        List<ProductData> productDatas = productDto.getAll(0, 10, 1).getData();
        assertEquals(productDatas.size(), 3);

        productDto.bulkAdd(productForms);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddDuplicateInputs() throws ApiException, JsonProcessingException {
        TestUtil.createBrand(brand, category);

        ProductForm productForm1 = TestUtil.getProductFormDto(barcode,brand,category,name,mrp);
        ProductForm productForm2 = TestUtil.getProductFormDto("abcdef13",brand,category,name,mrp);
        ProductForm productForm3 = TestUtil.getProductFormDto("abcdef13",brand,category,name,mrp);
        List<ProductForm> productForms = List.of(productForm1, productForm2, productForm3);

        productDto.bulkAdd(productForms);
        List<ProductData> productDatas = productDto.getAll(0, 10, 1).getData();
        assertEquals(productDatas.size(), 3);

        productDto.bulkAdd(productForms);
    }

}
