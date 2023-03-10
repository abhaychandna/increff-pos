package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.BrandFormErrorData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.ErrorUtil;
import com.increff.pos.util.PreProcessingUtil;




@Component
public class BrandDto {
    
    @Autowired
    private BrandService svc;

    public BrandData add(BrandForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        BrandPojo brand = convert(form);
        brand = svc.add(brand);
        return convert(brand); 
    }

    public void bulkAdd(List<BrandForm> forms) throws ApiException, JsonProcessingException {
        bulkAddValidate(forms);

        List<BrandPojo> validBrands = forms.stream().map(e->convert(e)).collect(Collectors.toList());
        svc.bulkAdd(validBrands);
    }

    public BrandData get(Integer id) throws ApiException {
        BrandPojo brand = svc.getCheck(id);
		return convert(brand);
    }

    public PaginatedData<BrandData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<BrandPojo> brands = svc.getAll(pageNo, pageSize);
        List<BrandData> brandDataList = brands.stream().map(brand->convert(brand)).collect(Collectors.toList());
        Integer count = svc.getRecordsCount();
        return new PaginatedData<BrandData>(brandDataList, draw, count, count);
    }

    public void update(Integer id, BrandForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        BrandPojo brand = convert(form);
        svc.update(id, brand);
    }
 
    private void bulkAddValidate(List<BrandForm> forms) throws JsonProcessingException, ApiException {
        checkDuplicateBrandCategoryPair(forms);
        checkBrandCategoryAlreadyExists(forms);
    }

    private void checkDuplicateBrandCategoryPair(List<BrandForm> forms) throws ApiException, JsonProcessingException {
        String duplicateBrandCategoryErrorMessage = "Duplicate Brand and Category pair";
        Boolean errorFound = false;
        MultiKeyMap brandCategoryMap = new MultiKeyMap();
        List<BrandFormErrorData> errors = new ArrayList<BrandFormErrorData>();

        for (BrandForm form : forms){
            try {
                PreProcessingUtil.normalizeAndValidate(form);
                if(brandCategoryMap.containsKey(form.getBrand(), form.getCategory())) throw new ApiException(duplicateBrandCategoryErrorMessage);
                brandCategoryMap.put(form.getBrand(), form.getCategory(), true);
                errors.add(new BrandFormErrorData(form.getBrand(), form.getCategory(), ""));
            } catch (ApiException e) {
                errorFound = true;
                errors.add(new BrandFormErrorData(form.getBrand(), form.getCategory(), e.getMessage()));
            }
        };
        if(errorFound) ErrorUtil.throwErrors(errors);
    }

	private BrandData convert(BrandPojo pojo) {
		return ConvertUtil.convert(pojo, BrandData.class);
	}

	private BrandPojo convert(BrandForm form) {
		return ConvertUtil.convert(form, BrandPojo.class);
	}

    private void checkBrandCategoryAlreadyExists(List<BrandForm> forms) throws ApiException, JsonProcessingException {
        String alreadyExistsErrorMessage = "Brand and Category pair already exists";
        
        List<String> brandList = forms.stream().map(e->e.getBrand()).collect(Collectors.toList());
        List<String> categoryList = forms.stream().map(e->e.getCategory()).collect(Collectors.toList());
        List<BrandPojo> existingBrands = svc.getByMultipleColumns(List.of("brand", "category"), List.of(brandList, categoryList));

        MultiKeyMap existingBrandCategoryMap = new MultiKeyMap();
        existingBrands.stream().forEach(brand-> existingBrandCategoryMap.put(brand.getBrand(), brand.getCategory(), true));
        List<BrandFormErrorData> errors = new ArrayList<BrandFormErrorData>();
        Boolean errorFound = false;

        for (BrandForm form : forms){
            if (existingBrandCategoryMap.containsKey(form.getBrand(), form.getCategory())) {
                errorFound = true;
                errors.add(new BrandFormErrorData(form.getBrand(), form.getCategory(), alreadyExistsErrorMessage));
            }
            else {
                errors.add(new BrandFormErrorData(form.getBrand(), form.getCategory(), ""));
            }
        };
        if(errorFound) ErrorUtil.throwErrors(errors);
    }

}
