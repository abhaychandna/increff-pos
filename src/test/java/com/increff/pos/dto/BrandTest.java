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
        BrandForm brandForm = TestUtil.getBrandFormDto("adidas", "tshirts");
        BrandData brandData = brandDto.add(brandForm);
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testAddNormalize() throws ApiException {
        BrandForm brandForm = TestUtil.getBrandFormDto(" ADIDAS ", "tshirts");
        BrandData brandData = brandDto.add(brandForm);
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testGetBrand() throws ApiException {
        BrandForm brandForm = TestUtil.getBrandFormDto("adidas", "tshirts");
        BrandData brandData = brandDto.add(brandForm);
        int id = brandData.getId();
        brandData = brandDto.get(id);
        assertEquals("adidas", brandData.getBrand());
        assertEquals("tshirts", brandData.getCategory());
    }

    @Test
    public void testGetAllBrand() throws ApiException {
        BrandForm brandForm = TestUtil.getBrandFormDto("B1", "C");
        brandDto.add(brandForm);
        brandForm = TestUtil.getBrandFormDto("B2", "C");
        brandDto.add(brandForm);
        brandForm = TestUtil.getBrandFormDto("B3", "C");
        brandDto.add(brandForm);

        List<BrandData> reqList = brandDto.getAll(0, 10);
        assertEquals(3, reqList.size());
    }

    @Test
    public void testUpdateBrand() throws ApiException {
        BrandForm brandForm = TestUtil.getBrandFormDto("adidas", "tshirts");
        BrandData brandData = brandDto.add(brandForm);
        brandForm.setCategory("hoodies");
        brandDto.update(brandData.getId(), brandForm);

        BrandData d = brandDto.get(brandData.getId());
        assertEquals("adidas", d.getBrand());
        assertEquals("hoodies", d.getCategory());
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
