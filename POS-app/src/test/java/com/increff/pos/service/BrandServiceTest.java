package com.increff.pos.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.TestUtil;

public class BrandServiceTest extends AbstractUnitTest {
    @Autowired
    private BrandService svc;
    @Autowired
    private TestUtil testUtil;
    
    private String brand;
    private String category;
    @Before
    public void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
    }

    @Test
    public void testAdd() throws ApiException {
        BrandPojo brandPojo = svc.add(new BrandPojo(brand, category));
        assertEquals(brand, brandPojo.getBrand());
        assertEquals(category, brandPojo.getCategory());
    }

    @Test
    public void testGetCheck() throws ApiException {
        BrandPojo brandPojo = svc.add(new BrandPojo(brand, category));
        BrandPojo brandPojoGet = svc.getCheck(brandPojo.getId());
        assertEquals(brandPojo.getId(), brandPojoGet.getId());
        assertEquals(brandPojo.getBrand(), brandPojoGet.getBrand());
        assertEquals(brandPojo.getCategory(), brandPojoGet.getCategory());
    }

    @Test
    public void testGetAll() throws ApiException {
        svc.add(new BrandPojo(brand, category));
        svc.add(new BrandPojo("brand2", "category2"));
        assertEquals(2, svc.getAll().size());
    }

    @Test
    public void testUpdate() throws ApiException {
        BrandPojo brandPojo = svc.add(new BrandPojo(brand, category));
        BrandPojo newBrandPojo = new BrandPojo("brand2", "category2");
        svc.update(brandPojo.getId(), newBrandPojo);
        BrandPojo brandPojoGet = svc.getCheck(brandPojo.getId());
        assertEquals(brandPojo.getId(), brandPojoGet.getId());
        assertEquals(brandPojo.getBrand(), brandPojoGet.getBrand());
        assertEquals(brandPojo.getCategory(), brandPojoGet.getCategory());
    }

    @Test
    public void testGetRecordsCount() throws ApiException {
        svc.add(new BrandPojo(brand, category));
        svc.add(new BrandPojo("brand2", "category2"));
        assertEquals(Integer.valueOf(2), svc.getRecordsCount());
    }

    @Test
    public void testGetCheckBrandCategory() throws ApiException {
        svc.add(new BrandPojo(brand, category));
        BrandPojo brandPojo = svc.getCheckBrandCategory(brand, category);
        assertEquals(brand, brandPojo.getBrand());
        assertEquals(category, brandPojo.getCategory());
    }

    @Test
    public void testGetCheckBrandCategoryNullBrand() throws ApiException {
        String expectedMessage = "Brand or Category cannot be null";
        Exception exception = assertThrows(ApiException.class, () -> {
            svc.getCheckBrandCategory(null, category);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }        

    @Test
    public void testGetCheckBrandCategoryNullCategory() throws ApiException {
        String expectedMessage = "Brand or Category cannot be null";
        Exception exception = assertThrows(ApiException.class, () -> {
            svc.getCheckBrandCategory(brand, null);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }
    
    @Test
    public void testAddExistingBrandCategory() throws ApiException {
        String expectedMessage = "Brand Category pair already exists";
        svc.add(new BrandPojo(brand, category));
        Exception exception = assertThrows(ApiException.class, () -> {
            svc.add(new BrandPojo(brand, category));
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testUpdateExistingBrandCategory() throws ApiException {
        String expectedMessage = "Brand Category pair already exists";
        BrandPojo brandPojo = svc.add(new BrandPojo(brand, category));
        BrandPojo newBrandPojo = svc.add(new BrandPojo("brand2", "category2"));
        Exception exception = assertThrows(ApiException.class, () -> {
            svc.update(brandPojo.getId(), newBrandPojo);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetCheckDoesNotExist() throws ApiException {
        Integer id = 1;
        String expectedMessage = "Brand with given ID does not exist, id: " + id;
        Exception exception = assertThrows(ApiException.class, () -> {
            svc.getCheck(1);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetByMultipleColumnDifferentColumnAndValueListSize() throws ApiException {
        String expectedMessage = "Column and value list size should be same";
        Exception exception = assertThrows(ApiException.class, () -> {
            svc.getByMultipleColumns(List.of("brand"), List.of(List.of("b1"),List.of("c1")));
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }
    
}
