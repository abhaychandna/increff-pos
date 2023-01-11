package com.increff.pos.dto;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

public class BrandTest extends AbstractUnitTest {
    @Autowired
    private BrandDto brandDto;

    @Test
    public void testAddBrand() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("adidas","tshirts");
        BrandData brandData = brandDto.add(f);
        assertEquals("adidas",brandData.getBrand());
        assertEquals("tshirts",brandData.getCategory());
    }

    @Test
    public void testAddNormalize() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto(" ADIDAS ","tshirts");
        BrandData brandData = brandDto.add(f);
        assertEquals("adidas",brandData.getBrand());
        assertEquals("tshirts",brandData.getCategory());
    }

    @Test
    public void testGetBrand() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("adidas","tshirts");
        BrandData brandData = brandDto.add(f);
        int id = brandData.getId();
        brandData = brandDto.get(id);
        assertEquals("adidas",brandData.getBrand());
        assertEquals("tshirts",brandData.getCategory());
    }

    @Test
    public void testGetAllBrand() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("B1","C");
        brandDto.add(f);
        f = TestUtil.getBrandFormDto("B2","C");
        brandDto.add(f);
        f = TestUtil.getBrandFormDto("B3","C");
        brandDto.add(f);
        
        List<BrandData> reqList = brandDto.getAll();
        assertEquals(3,reqList.size());
    }

    @Test
    public void testUpdateBrand() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("adidas","tshirts");
        BrandData brandData = brandDto.add(f);
        f.setCategory("hoodies");
        brandDto.update(brandData.getId(),f);

        BrandData d = brandDto.get(brandData.getId());
        assertEquals("adidas",d.getBrand());
        assertEquals("hoodies",d.getCategory());
    }

    @Test
    public void testAddDuplicateBrandCategory() throws ApiException {
        BrandForm f = TestUtil.getBrandFormDto("adidas","tshirts");
        BrandData brandData = brandDto.add(f);
        
        // Adding same branc/category twice shuld throw error 
        boolean error = false;
        try{
            brandDto.add(f);
        }
        catch (ApiException e){
            error = true;
        }
        assertTrue(error);
        //TODO : Assert.Fail
    }

}
