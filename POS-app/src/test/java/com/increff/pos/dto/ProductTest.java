package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.ProductPutForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.util.TestUtil;

public class ProductTest extends AbstractUnitTest{
    
    @Autowired
    private ProductDto productDto;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private TestUtil testUtil;
    
    private String brand;
    private String category;
    private String barcode;
    private String name;
    private Double mrp;
    @Before
    public void init() throws ApiException{
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.5;
    }

    @Test
    public void testAddProduct() throws ApiException{
        BrandData brandData = testUtil.createBrand(brand, category);
        ProductForm productForm = testUtil.getProductForm(barcode,brand,category,name,mrp);
        ProductData productData = productDto.add(productForm);
        
        ProductPojo product = productDao.select(productData.getId());
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(name, product.getName());
        assertEquals(mrp, product.getMrp());
    }

    @Test
    public void testNormalizeUppercaseToLowercase() throws ApiException {
        BrandData brandData = testUtil.createBrand(brand, category);
        String newBrand = brand.toUpperCase(), newCategory = category.toUpperCase();
        ProductData productData = productDto.add(testUtil.getProductForm(barcode,newBrand,newCategory,name,mrp)); 
        
        ProductPojo product = productDao.select(productData.getId());
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(name, product.getName());
        assertEquals(mrp, product.getMrp());
    }

    @Test
    public void testNormalizeTrim() throws ApiException {
        BrandData brandData = testUtil.createBrand(brand, category);
        String newBrand = " " + brand + " ", newCategory = " " + category + " ";
        ProductData productData = productDto.add(testUtil.getProductForm(barcode,newBrand,newCategory,name,mrp)); 
        
        ProductPojo product = productDao.select(productData.getId());
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(name, product.getName());
        assertEquals(mrp, product.getMrp());
    }

    @Test
    public void testValidateEmptyBrand() throws ApiException{
        ProductForm productForm = testUtil.getProductForm(barcode,"",category,name,mrp);
        String expectedMessage = testUtil.createFieldEmptyErrorMessage("Brand");
        Exception exception = assertThrows(ApiException.class, () -> {
            productDto.add(productForm);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testValidateEmptyName() throws ApiException{
        ProductForm productForm = testUtil.getProductForm(barcode,brand,category,"",mrp);
        String expectedMessage = testUtil.createFieldEmptyErrorMessage("Name");
        Exception exception = assertThrows(ApiException.class, () -> {
            productDto.add(productForm);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testAddBrandCategoryDoesNotExist() throws ApiException{
        ProductForm productForm = testUtil.getProductForm(barcode,brand,category,name,mrp);
        String expectedMessage = "Brand Category pair does not exist";
        Exception exception = assertThrows(ApiException.class, () -> {
            productDto.add(productForm);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testDuplicateBarcode() throws ApiException{
        testUtil.createProductCascade(barcode, brand, category, name, mrp);
        ProductForm productForm = testUtil.getProductForm(barcode,brand,category,name,mrp);
        String expectedMessage = "Product with barcode: " + barcode + " already exists";
        Exception exception = assertThrows(ApiException.class, () -> {
            productDto.add(productForm);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

     
    @Test
    public void testGet() throws ApiException {
        ProductPojo product = testUtil.createProductCascade(barcode, brand, category, name, mrp);
        ProductData productData = productDto.get(product.getId());
        
        assertEquals(product.getId(), productData.getId());
        assertEquals(brand, productData.getBrand());
        assertEquals(category, productData.getCategory());
        assertEquals(product.getBarcode(), productData.getBarcode());
        assertEquals(product.getName(), productData.getName());
        assertEquals(product.getMrp(), productData.getMrp());
    }

    @Test
    public void testGetAll() throws ApiException {
        testUtil.createProductCascade(barcode, brand, category, name, mrp);
        
        testUtil.createProduct("abcdef13", brand, category, name, mrp);
        PaginatedData<ProductData> d = productDto.getAll(0,10, 1);
        assertEquals(d.getData().size(), 2);
    }

    @Test
    public void testUpdate() throws ApiException {
        ProductPojo product = testUtil.createProductCascade(barcode, brand, category, name, mrp);
        ProductData productData = productDto.get(product.getId());
        
        String newName = "polo";
        Double newMrp = 200.5;
        
        ProductPutForm form = new ProductPutForm(newName, newMrp);
        productDto.update(productData.getId(), form);
        
        productData = productDto.get(product.getId());
        assertEquals(product.getId(), productData.getId());
        assertEquals(newName, productData.getName());
        assertEquals(newMrp, productData.getMrp());
    }

    @Test
    public void testGetByBarcode() throws ApiException {
        ProductPojo product = testUtil.createProductCascade(barcode, brand, category, name, mrp);
        ProductData productData = productDto.getByBarcode(product.getBarcode());
        
        assertEquals(product.getId(), productData.getId());
        assertEquals(brand, productData.getBrand());
        assertEquals(category, productData.getCategory());
        assertEquals(product.getBarcode(), productData.getBarcode());
        assertEquals(product.getName(), productData.getName());
        assertEquals(product.getMrp(), productData.getMrp());
    }


    @Test
    public void testBulkAdd() throws ApiException, JsonProcessingException{
        testUtil.createBrand(brand, category);

        ProductForm productForm1 = testUtil.getProductForm(barcode,brand,category,name,mrp);
        ProductForm productForm2 = testUtil.getProductForm("abcdef13",brand,category,name,mrp);
        ProductForm productForm3 = testUtil.getProductForm("abcdef14",brand,category,name,mrp);
        List<ProductForm> productForms = List.of(productForm1, productForm2, productForm3);

        productDto.bulkAdd(productForms);
        List<ProductData> productDataList = productDto.getAll(0, 10, 1).getData();
        assertEquals(productDataList.size(), 3);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddExisting() throws ApiException, JsonProcessingException {
        testUtil.createBrand(brand, category);

        ProductForm productForm1 = testUtil.getProductForm(barcode,brand,category,name,mrp);
        ProductForm productForm2 = testUtil.getProductForm("abcdef13",brand,category,name,mrp);
        ProductForm productForm3 = testUtil.getProductForm("abcdef14",brand,category,name,mrp);
        List<ProductForm> productForms = List.of(productForm1, productForm2, productForm3);

        productDto.bulkAdd(productForms);
        List<ProductData> productDataList = productDto.getAll(0, 10, 1).getData();
        assertEquals(productDataList.size(), 3);

        productDto.bulkAdd(productForms);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddDuplicateInputs() throws ApiException, JsonProcessingException {
        testUtil.createBrand(brand, category);

        ProductForm productForm1 = testUtil.getProductForm(barcode,brand,category,name,mrp);
        ProductForm productForm2 = testUtil.getProductForm("abcdef13",brand,category,name,mrp);
        ProductForm productForm3 = testUtil.getProductForm("abcdef13",brand,category,name,mrp);
        List<ProductForm> productForms = List.of(productForm1, productForm2, productForm3);

        productDto.bulkAdd(productForms);
        List<ProductData> productDataList = productDto.getAll(0, 10, 1).getData();
        assertEquals(productDataList.size(), 3);

        productDto.bulkAdd(productForms);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddBrandCategoryDoesNotExist() throws ApiException, JsonProcessingException {
        testUtil.createBrand(brand, category);

        ProductForm productForm1 = testUtil.getProductForm(barcode,brand,category,name,mrp);
        ProductForm productForm2 = testUtil.getProductForm("abcdef13",brand,category,name,mrp);
        ProductForm productForm3 = testUtil.getProductForm("abcdef14","brandDoesntExist",category,name,mrp);
        List<ProductForm> productForms = List.of(productForm1, productForm2, productForm3);
        productDto.bulkAdd(productForms);
    }

}
