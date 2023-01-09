package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;



@Component
public class ProductDto {
    
    @Autowired
    ProductService svc;
    @Autowired
    BrandService brandService;

    public ProductPojo add(ProductForm f) throws ApiException {
        ProductPojo p = ConvertUtil.convert(f);
        NormalizeUtil.normalize(p);
		ValidateUtil.validateProduct(p);
        return svc.add(p);
    }

    public ProductData get(int id) throws ApiException {
        ProductPojo p = svc.get(id);
		return ConvertUtil.convert(p);
    }

    public List<ProductData> getAll() throws ApiException{
        List<ProductPojo> Products = svc.getAll();
        List<ProductData> respList = new ArrayList<ProductData>();
        for (ProductPojo p : Products) {
            respList.add(ConvertUtil.convert(p));
        }
        return respList;
    }

    public void update(int id, ProductForm f) throws ApiException {
        ProductPojo p = ConvertUtil.convert(f);
        NormalizeUtil.normalize(p);
		ValidateUtil.validateProduct(p);
        svc.update(id, p);
    }





}
