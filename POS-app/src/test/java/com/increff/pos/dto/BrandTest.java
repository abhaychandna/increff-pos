package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.TestUtil;

public class BrandTest extends AbstractUnitTest {
    @Autowired
    private BrandDto brandDto;
    @Autowired
    private TestUtil testUtil;

    @Test
    public void testAddBrand() throws ApiException {
        BrandData brandData = testUtil.createBrand("adidas", "tshirts");
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testNormalizeUppercaseToLowercase() throws ApiException {
        BrandData brandData = brandDto.add(testUtil.getBrandForm("ADIDAS", "TshirTs"));
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testNormalizeTrim() throws ApiException {
        BrandData brandData = brandDto.add(testUtil.getBrandForm("  adidas  ", "  tshirts  "));
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testValidateEmptyBrand() throws ApiException {        
        String expectedMessage = testUtil.createFieldEmptyErrorMessage("Brand");
        Exception exception = assertThrows(ApiException.class, () -> {
            brandDto.add(testUtil.getBrandForm("", "Tshirts"));
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }
    @Test
    public void testValidateEmptyCategory() throws ApiException {        
        String expectedMessage = testUtil.createFieldEmptyErrorMessage("Category");
        Exception exception = assertThrows(ApiException.class, () -> {
            brandDto.add(testUtil.getBrandForm("adidas", ""));
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetBrand() throws ApiException {
        BrandData brandData = testUtil.createBrand("adidas", "tshirts");
        brandData = brandDto.get(brandData.getId());
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testGetAllBrand() throws ApiException {
        testUtil.createBrand("adidas", "tshirts");
        testUtil.createBrand("nike", "tshirts");
        testUtil.createBrand("puma", "tshirts");
        PaginatedData<BrandData> brandSearchData = brandDto.getAll(0, 10, 0);
        assertEquals(3, brandSearchData.getData().size());
    }

    @Test
    public void testUpdateBrand() throws ApiException {
        BrandForm brandForm = testUtil.getBrandForm("adidas", "tshirts");
        BrandData brandData = brandDto.add(brandForm);
        brandForm.setCategory("hoodies");
        brandDto.update(brandData.getId(), brandForm);

        brandData = brandDto.get(brandData.getId());
        assertEquals("adidas", brandData.getBrand());
        assertEquals("hoodies", brandData.getCategory());
    }

    @Test(expected = ApiException.class)
    public void testAddExistingBrandCategory() throws ApiException {
        BrandForm brandForm = testUtil.getBrandForm("adidas", "tshirts");
        brandDto.add(brandForm);

        brandDto.add(brandForm);
    }

    @Test
    public void testBulkAdd() throws ApiException, JsonProcessingException {
        BrandForm brandForm1 = testUtil.getBrandForm("adidas", "tshirts");
        BrandForm brandForm2 = testUtil.getBrandForm("nike", "tshirts");
        BrandForm brandForm3 = testUtil.getBrandForm("puma", "tshirts");
        List<BrandForm> brandForms = List.of(brandForm1, brandForm2, brandForm3);
        brandDto.bulkAdd(brandForms);

        PaginatedData<BrandData> brandSearchData = brandDto.getAll(0, 10, 0);
        assertEquals(3, brandSearchData.getData().size());
    }

    @Test(expected = ApiException.class)
    public void testBulkAddExisting() throws ApiException, JsonProcessingException {
        BrandForm brandForm1 = testUtil.getBrandForm("adidas", "tshirts");
        BrandForm brandForm2 = testUtil.getBrandForm("nike", "tshirts");
        BrandForm brandForm3 = testUtil.getBrandForm("puma", "tshirts");
        List<BrandForm> brandForms = List.of(brandForm1, brandForm2, brandForm3);
        brandDto.bulkAdd(brandForms);

        // Adding same brand/category twice should throw error
        brandDto.bulkAdd(brandForms);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddDuplicateInputs() throws ApiException, JsonProcessingException {
        BrandForm brandForm1 = testUtil.getBrandForm("adidas", "tshirts");
        List<BrandForm> brandForms = List.of(brandForm1, brandForm1);
        brandDto.bulkAdd(brandForms);
    }

}
