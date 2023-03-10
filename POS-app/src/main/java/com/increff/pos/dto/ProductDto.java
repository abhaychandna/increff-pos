package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.data.ProductFormErrorData;
import com.increff.pos.model.form.ProductPutForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.ErrorUtil;
import com.increff.pos.util.PreProcessingUtil;

@Component
public class ProductDto {

    @Autowired
    private ProductService svc;
    @Autowired
    private BrandService brandService;
    
    public ProductData add(ProductForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        ProductPojo product = convert(form);
        ProductPojo productPojo = svc.add(product);
        return convert(productPojo);
    }

    public void bulkAdd(List<ProductForm> forms) throws ApiException, JsonProcessingException {
        bulkAddValidate(forms);       
        List<ProductPojo> validProducts = convertBulk(forms);
        svc.bulkAdd(validProducts);   
    }

    public ProductData get(Integer id) throws ApiException {
        ProductPojo product = svc.getCheck(id);
        return convert(product);
    }

    public PaginatedData<ProductData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<ProductPojo> products = svc.getAll(pageNo, pageSize);
        List<ProductData> productDataList = new ArrayList<ProductData>();
        for (ProductPojo product : products) {
            productDataList.add(convert(product));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<ProductData>(productDataList, draw, count, count);
    }

    public void update(Integer id, ProductPutForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        svc.update(id, form.getName(), form.getMrp());
    }

    public ProductData getByBarcode(String barcode) throws ApiException {
        return convert(svc.getCheckBarcode(barcode));
    }

    private List<ProductPojo> convertBulk(List<ProductForm> forms) throws JsonProcessingException, ApiException {
        List<ProductFormErrorData> errors = new ArrayList<ProductFormErrorData>();
        Boolean errorFound = false;
        List<String> brandsList = forms.stream().map(ProductForm::getBrand).collect(Collectors.toList());
        List<String> categoriesList = forms.stream().map(ProductForm::getCategory).collect(Collectors.toList());

        List<BrandPojo> brands = brandService.getByMultipleColumns(List.of("brand", "category"), List.of(brandsList, categoriesList));
        MultiKeyMap brandCategoryToId = new MultiKeyMap();
        brands.stream().forEach(brand -> brandCategoryToId.put(brand.getBrand(), brand.getCategory(), brand.getId()));

        List<ProductPojo> validProducts = new ArrayList<ProductPojo>();
        for (ProductForm form : forms){
            ProductPojo product = ConvertUtil.convert(form, ProductPojo.class);
            Integer brandCategoryId = (Integer) brandCategoryToId.get(form.getBrand(), form.getCategory());
            if(Objects.isNull(brandCategoryId))  {
                errorFound = true;
                errors.add(new ProductFormErrorData(form.getBarcode(), form.getBrand(), form.getCategory(), form.getName(), form.getMrp(), "Brand Category pair does not exist"));
            }else{
                product.setBrandCategory(brandCategoryId);
                validProducts.add(product);
                errors.add(new ProductFormErrorData(form.getBarcode(), form.getBrand(), form.getCategory(), form.getName(), form.getMrp(), ""));
            }
        };
        if(errorFound) ErrorUtil.throwErrors(errors);
        return validProducts;
    }

    private void bulkAddValidate(List<ProductForm> forms) throws JsonProcessingException, ApiException {
        checkDuplicateBarcodes(forms);
        checkBarcodeAlreadyExists(forms);
    }

    private void checkDuplicateBarcodes(List<ProductForm> forms) throws ApiException, JsonProcessingException {
        List<ProductFormErrorData> errors = new ArrayList<ProductFormErrorData>();
        Boolean errorFound = false;
        Set<String> barcodeSet = new HashSet<String>();
        for(ProductForm form : forms) {
            try {
                PreProcessingUtil.normalizeAndValidate(form);
                if(barcodeSet.contains(form.getBarcode()))throw new ApiException("Duplicate Barcodes not allowed");
                barcodeSet.add(form.getBarcode());
                errors.add(new ProductFormErrorData(form.getBarcode(), form.getBrand(), form.getCategory(), form.getName(), form.getMrp(), ""));
            } catch (ApiException e) {
                errorFound = true;
                errors.add(new ProductFormErrorData(form.getBarcode(), form.getBrand(), form.getCategory(), form.getName(), form.getMrp(), e.getMessage()));
            }
        };

        if(errorFound) ErrorUtil.throwErrors(errors);
    }

    private void checkBarcodeAlreadyExists(List<ProductForm> forms) throws JsonProcessingException, ApiException {
        List<ProductFormErrorData> errors = new ArrayList<ProductFormErrorData>();
        Boolean errorFound = false;

        Set<String> barcodeSet = forms.stream().map(ProductForm::getBarcode).collect(Collectors.toSet());
        List<ProductPojo> products = svc.getByColumn("barcode", barcodeSet.stream().collect(Collectors.toList()));
        Set<String> existingBarcodes = products.stream().map(ProductPojo::getBarcode).collect(Collectors.toSet());

        for (ProductForm form : forms){
            if(existingBarcodes.contains(form.getBarcode())){
                errors.add(new ProductFormErrorData(form.getBarcode(), form.getBrand(), form.getCategory(), form.getName(), form.getMrp(), "Barcode already exists"));
                errorFound = true;
            }
            else
                errors.add(new ProductFormErrorData(form.getBarcode(), form.getBrand(), form.getCategory(), form.getName(), form.getMrp(), ""));
        };
        if(errorFound) ErrorUtil.throwErrors(errors);
    }

    private ProductPojo convert(ProductForm productForm) throws ApiException{
		ProductPojo product = ConvertUtil.convert(productForm, ProductPojo.class);
		product.setBrandCategory(brandService.getCheckBrandCategory(productForm.getBrand(), productForm.getCategory()).getId());
		return product;
	}

    private ProductData convert(ProductPojo product) throws ApiException{
        ProductData productData = ConvertUtil.convert(product, ProductData.class);
        BrandPojo brand = brandService.getCheck(product.getBrandCategory());
        productData.setBrand(brand.getBrand());
        productData.setCategory(brand.getCategory());
        return productData;
    }

}
