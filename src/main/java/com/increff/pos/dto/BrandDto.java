package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.BrandFormErrorData;
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

    public void bulkAdd(List<BrandForm> forms) throws ApiException, JsonProcessingException {
        normalizeAndValidate(forms);
        checkDuplicateBrandCategoryPairs(forms);
        checkBrandCategoryAlreadyExists(forms);

        List<BrandPojo> validBrands = forms.stream().map(e->convert(e)).collect(Collectors.toList());
        svc.bulkAdd(validBrands);
    }
 
    public BrandData get(Integer id) throws ApiException {
        BrandPojo brand = svc.get(id);
		return convert(brand);
    }

    public PaginatedData<BrandData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
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

    private void normalizeAndValidate(List<BrandForm> forms) throws ApiException, JsonProcessingException {
    
        List<BrandFormErrorData> errors = new ArrayList<BrandFormErrorData>();
        forms.forEach(form->{
            try {
                PreProcessingUtil.normalizeAndValidate(form);
            } catch (ApiException e) {
                errors.add(new BrandFormErrorData(form.getBrand(), form.getCategory(), e.getMessage()));
            }
        });
        if(errors.size() > 0 ) throwErrors(errors);
    }

    private void checkDuplicateBrandCategoryPairs(List<BrandForm> forms) throws ApiException, JsonProcessingException {
        String separator = "_", errorName = "Duplicate Brand and Category pair";
        List<BrandFormErrorData> errors = new ArrayList<BrandFormErrorData>();
        Set<String> brandCategorySet = new HashSet<String>();
        forms.forEach(form->{
            String brandCategory = form.getBrand() + separator + form.getCategory();
            if (brandCategorySet.contains(brandCategory)) {
                errors.add(new BrandFormErrorData(form.getBrand(), form.getCategory(), errorName));
            }
            brandCategorySet.add(brandCategory);
        });
        if(errors.size() > 0 ) throwErrors(errors);
    }

    private void checkBrandCategoryAlreadyExists(List<BrandForm> forms) throws ApiException, JsonProcessingException {
        String separator = "_", errorName = "Brand and Category pair already exists";
        List<String> brandList = forms.stream().map(e->e.getBrand()).collect(Collectors.toList());
        List<BrandPojo> existingBrands = svc.getByColumn("brand", brandList);
        Set<String> existingBrandCategorySet = existingBrands.stream().map(brand->brand.getBrand() + separator + brand.getCategory()).collect(Collectors.toSet());;
        Set<String> intersectingBrandCategorySet = forms.stream().map(brand->brand.getBrand() + separator + brand.getCategory()).filter(existingBrandCategorySet::contains).collect(Collectors.toSet());
        
        List<BrandFormErrorData> errors = new ArrayList<BrandFormErrorData>();
        forms.forEach(form->{
            String brandCategory = form.getBrand() + separator + form.getCategory();
            if (intersectingBrandCategorySet.contains(brandCategory)) {
                errors.add(new BrandFormErrorData(form.getBrand(), form.getCategory(), errorName));
            }
        });
        if(errors.size() > 0 ) throwErrors(errors);
    }

    private void throwErrors(List<BrandFormErrorData> errors) throws ApiException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
        throw new ApiException(json);
    }



}
