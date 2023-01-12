package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class BrandTest extends AbstractUnitTest {
    @Autowired
    private BrandDto brandDto;

    @Test
    public void testAddBrand() throws ApiException {
        BrandData brandData = TestUtil.createBrand("adidas", "tshirts");
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testAddNormalize() throws ApiException {
        BrandData brandData = TestUtil.createBrand(" ADIDAS ", " TshirTs");
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testGetBrand() throws ApiException {
        BrandData brandData = TestUtil.createBrand("adidas", "tshirts");
        brandData = brandDto.get(brandData.getId());
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testGetAllBrand() throws ApiException {
        TestUtil.createBrand("adidas", "tshirts");
        TestUtil.createBrand("nike", "tshirts");
        TestUtil.createBrand("puma", "tshirts");
        BrandSearchData brandSearchData = brandDto.getAll(0, 10, 0);
        assertEquals(3, brandSearchData.getData().size());
    }

    @Test
    public void testUpdateBrand() throws ApiException {
        BrandForm brandForm = TestUtil.getBrandFormDto("adidas", "tshirts");
        BrandData brandData = brandDto.add(brandForm);
        brandForm.setCategory("hoodies");
        brandDto.update(brandData.getId(), brandForm);

        brandData = brandDto.get(brandData.getId());
        assertEquals("adidas", brandData.getBrand());
        assertEquals("hoodies", brandData.getCategory());
    }

    @Test
    public void testAddDuplicateBrandCategory() throws ApiException {
        BrandForm brandForm = TestUtil.getBrandFormDto("adidas", "tshirts");
        brandDto.add(brandForm);

        // Adding same brand/category twice should throw error
        try {
            brandDto.add(brandForm);
        } catch (ApiException e) {
            // Test passed if add throws error
            return;
        }
        fail();
    }

}
