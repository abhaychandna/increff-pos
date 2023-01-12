package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;

@Component
public class ProductDto {

    @Autowired
    private ProductService svc;

    public ProductData add(ProductForm form) throws ApiException {
        ProductPojo product = ConvertUtil.convert(form);
        NormalizeUtil.normalize(product);
        ValidateUtil.validateProduct(product);
        ProductPojo productPojo = svc.add(product);
        return ConvertUtil.convert(productPojo);
    }

    public ProductData get(Integer id) throws ApiException {
        ProductPojo product = svc.get(id);
        return ConvertUtil.convert(product);
    }

    public List<ProductData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<ProductPojo> Products = svc.getAll(pageNo, pageSize);
        List<ProductData> respList = new ArrayList<ProductData>();
        for (ProductPojo p : Products) {
            respList.add(ConvertUtil.convert(p));
        }
        return respList;
    }

    public void update(Integer id, ProductForm form) throws ApiException {
        ProductPojo product = ConvertUtil.convert(form);
        NormalizeUtil.normalize(product);
        ValidateUtil.validateProduct(product);
        svc.update(id, product);
    }

}
