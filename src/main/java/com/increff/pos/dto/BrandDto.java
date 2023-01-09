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
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
@Component
public class BrandDto {
    
    @Autowired
    BrandService svc;

    public BrandPojo add(BrandForm f) throws ApiException {
        BrandPojo p = ConvertUtil.convert(f);
        NormalizeUtil.normalize(p);
		validate(p);
        return svc.add(p);
    }

    public BrandData get(int id) throws ApiException {
        BrandPojo p = svc.get(id);
		return ConvertUtil.convert(p);
    }

    public List<BrandData> getAll(){
        List<BrandPojo> brands = svc.getAll();
        List<BrandData> respList = new ArrayList<BrandData>();
        for (BrandPojo p : brands) {
            respList.add(ConvertUtil.convert(p));
        }
        return respList;
    }

    public void update(int id, BrandForm f) throws ApiException {
        BrandPojo p = ConvertUtil.convert(f);
        NormalizeUtil.normalize(p);
		validate(p);
        svc.update(id, p);
    }




    // QUES : Create ValidateUtil for this too ?? 
	protected static void validate(BrandPojo p) throws ApiException {
		if(StringUtil.isEmpty(p.getBrand()))
			throw new ApiException("Brand cannot be empty");
		if(StringUtil.isEmpty(p.getCategory()))
			throw new ApiException("Category cannot be empty");
	}
}
