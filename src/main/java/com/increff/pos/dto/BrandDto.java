package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
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
        BrandPojo brand = ConvertUtil.convert(f);
        NormalizeUtil.normalize(brand);
		ValidateUtil.validateBrand(brand);
        brand = svc.add(brand);
        return ConvertUtil.convert(brand); 
    }

    public void bulkAdd(List<BrandForm> forms) throws ApiException {
        /*
         * Design of bulk 
         * What if some fail dto level validations (Eg null etc)
         * What if some fail service level validations (Eg duplicate)
         * What if some fail db level validations (Eg same brand category uploaded twice. Whule checking db it will say not null,
         * but while adding it will give error)
         */  
    }

    public BrandData get(Integer id) throws ApiException {
        BrandPojo brand = svc.get(id);
		return ConvertUtil.convert(brand);
    }

    public List<BrandData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<BrandPojo> brands = svc.getAll(pageNo, pageSize);
        List<BrandData> respList = new ArrayList<BrandData>();
        for (BrandPojo brand : brands) {
            respList.add(ConvertUtil.convert(brand));
        }
        return respList;
    }

    public void update(Integer id, BrandForm f) throws ApiException {
        BrandPojo brand = ConvertUtil.convert(f);
        NormalizeUtil.normalize(brand);
		ValidateUtil.validateBrand(brand);
        svc.update(id, brand);
    }





}
