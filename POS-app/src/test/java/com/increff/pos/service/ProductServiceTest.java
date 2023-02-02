package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.pojo.ProductPojo;

public class ProductServiceTest extends AbstractUnitTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private TestUtil testUtil;

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
    public void testAdd() throws ApiException {
        BrandData brandData = testUtil.createBrand(brand, category);
        ProductPojo product = new ProductPojo(barcode, brandData.getId(), name, mrp);
        productService.add(product);
        product = productDao.select(ProductPojo.class, product.getId());
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(name, product.getName());
        assertEquals(mrp, product.getMrp());
    }

    @Test
    public void testGet() throws ApiException {
        BrandData brandData = testUtil.createBrand(brand, category);
        ProductPojo product = new ProductPojo(barcode, brandData.getId(), name, mrp);
        productService.add(product);
        product = productService.get(product.getId());
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(name, product.getName());
        assertEquals(mrp, product.getMrp());
    }

    @Test
    public void testUpdate() throws ApiException {
        BrandData brandData = testUtil.createBrand(brand, category);
        ProductPojo product = new ProductPojo(barcode, brandData.getId(), name, mrp);
        productService.add(product);
        product = productService.get(product.getId());

        Double newMrp = 200.5;
        String newName = "polo tshirt";
        product.setMrp(newMrp);
        product.setName(newName);
        productService.update(product.getId(), product);
        product = productService.get(product.getId());
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(newName, product.getName());
        assertEquals(newMrp, product.getMrp());
    }

    @Test
    public void testGetCheckBarcode() throws ApiException {
        BrandData brandData = testUtil.createBrand(brand, category);
        ProductPojo product = new ProductPojo(barcode, brandData.getId(), name, mrp);
        productService.add(product);
        product = productService.getCheckBarcode(barcode);
        assertEquals(brandData.getId(), product.getBrandCategory());
        assertEquals(barcode, product.getBarcode());
        assertEquals(name, product.getName());
        assertEquals(mrp, product.getMrp());
    }

    @Test
    public void testGetCheckBarcodeAlreadyExists() throws ApiException {
        BrandData brandData = testUtil.createBrand(brand, category);
        ProductPojo product = new ProductPojo(barcode, brandData.getId(), name, mrp);
        productService.add(product);
        String expectedMessage = "Product with barcode: " + barcode + " already exists";
        Exception exception = assertThrows(ApiException.class, () -> {
            productService.add(product);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetCheckDoesNotExist() throws ApiException {
        Integer id = 1;
        String expectedMessage = "Product with id: " +  id + " does not exist";
        Exception exception = assertThrows(ApiException.class, () -> {
            productService.get(id);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }
    
}