package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;

@Component
public class ProductDto {

    @Autowired
    private ProductService svc;
    @Autowired
    private BrandService brandService;
    
    public ProductData add(ProductForm form) throws ApiException {
        ProductPojo product = convert(form);
        NormalizeUtil.normalize(product);
        ValidateUtil.validateProduct(product);
        ProductPojo productPojo = svc.add(product);
        return convert(productPojo);
    }

    public ProductData get(Integer id) throws ApiException {
        ProductPojo product = svc.get(id);
        return convert(product);
    }

    public List<ProductData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<ProductPojo> products = svc.getAll(pageNo, pageSize);
        List<ProductData> productList = new ArrayList<ProductData>();
        for (ProductPojo p : products) {
            productList.add(convert(p));
        }
        return productList;
    }

    public void update(Integer id, ProductForm form) throws ApiException {
        ProductPojo product = convert(form);
        NormalizeUtil.normalize(product);
        ValidateUtil.validateProduct(product);
        svc.update(id, product);
    }

    private ProductPojo convert(ProductForm productForm) throws ApiException{
		ProductPojo product = ConvertUtil.convert(productForm, ProductPojo.class);
		product.setBrandCategory(brandService.getCheckBrandCategory(productForm.getBrand(), productForm.getCategory()).getId());
		return product;
	}

    private ProductData convert(ProductPojo product) throws ApiException{
        ProductData productData = ConvertUtil.convert(product, ProductData.class);
        BrandPojo brand = brandService.get(product.getBrandCategory());
        productData.setBrand(brand.getBrand());
        productData.setCategory(brand.getCategory());
        return productData;
    }
}
