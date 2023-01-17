package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.BrandBulkAddData;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PreProcessingUtil;


@Component
public class BrandDto {
    
    @Autowired
    BrandService svc;

    public BrandData add(BrandForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        BrandPojo brand = convert(form);
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
                PreProcessingUtil.normalizeAndValidate(form);
                BrandPojo brand = convert(form);
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

    public PaginatedData<BrandData> getAll(Integer start, Integer length, Integer draw) throws ApiException {
        Integer pageNo = start/length;
        Integer pageSize = length;
        List<BrandPojo> brands = svc.getAll(pageNo, pageSize);
        List<BrandData> brandDatas = new ArrayList<BrandData>();
        for (BrandPojo brand : brands) {
            brandDatas.add(convert(brand));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<BrandData>(brandDatas, draw, count, count);
    }

    public void update(Integer id, BrandForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        BrandPojo brand = convert(form);
        svc.update(id, brand);
    }

	private BrandData convert(BrandPojo p) {
		return ConvertUtil.convert(p, BrandData.class);
	}

	private BrandPojo convert(BrandForm f) {
		return ConvertUtil.convert(f, BrandPojo.class);
	}



}
