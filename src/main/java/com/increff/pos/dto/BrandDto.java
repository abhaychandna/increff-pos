package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.BrandBulkAddData;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.BrandSearchData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;


@Component
public class BrandDto {
    
    @Autowired
    BrandService svc;

    public BrandData add(BrandForm f) throws ApiException {
        BrandPojo brand = convert(f);
        NormalizeUtil.normalize(brand);
		ValidateUtil.validateBrand(brand);
        brand = svc.add(brand);
        return convert(brand); 
    }

    // QUES : Is this format for bulkAdd correct ??
    public List<BrandBulkAddData> bulkAdd(List<BrandForm> forms) throws ApiException {
        BrandBulkAddData brandBulkAddData = new BrandBulkAddData();
        List<BrandBulkAddData> errors = new ArrayList<BrandBulkAddData>();
        List<BrandPojo> validBrands = new ArrayList<BrandPojo>();
        for (BrandForm form : forms) {
            try{
                
                BrandPojo brand = convert(form);
                NormalizeUtil.normalize(brand);
                ValidateUtil.validateBrand(brand);
                brandBulkAddData.setBrand(brand.getBrand());
                brandBulkAddData.setCategory(brand.getCategory());
                validBrands.add(brand);
            }
            catch(ApiException e){
                brandBulkAddData.setError(e.getMessage());
                errors.add(brandBulkAddData);
            }
        }
        errors = svc.bulkAdd(validBrands, errors);
        return errors;
    }
 
    public BrandData get(Integer id) throws ApiException {
        BrandPojo brand = svc.get(id);
		return convert(brand);
    }

    public BrandSearchData getAll(Integer pageNo, Integer pageSize, Integer draw) throws ApiException {
        List<BrandPojo> brands = svc.getAll(pageNo, pageSize);
        List<BrandData> respList = new ArrayList<BrandData>();
        for (BrandPojo brand : brands) {
            respList.add(convert(brand));
        }

        BrandSearchData brandSearchData= new BrandSearchData();
        brandSearchData.setData(respList);
        brandSearchData.setDraw(draw);
        Integer count = svc.getRecordsCount();
        brandSearchData.setRecordsFiltered(count);
        brandSearchData.setRecordsTotal(count);
        return brandSearchData;
    }

    public void update(Integer id, BrandForm f) throws ApiException {
        BrandPojo brand = convert(f);
        NormalizeUtil.normalize(brand);
		ValidateUtil.validateBrand(brand);
        svc.update(id, brand);
    }

	private BrandData convert(BrandPojo p) {
		return ConvertUtil.convert(p, BrandData.class);
	}

	private BrandPojo convert(BrandForm f) {
		return ConvertUtil.convert(f, BrandPojo.class);
	}



}
