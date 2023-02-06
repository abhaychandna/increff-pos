package com.increff.pos.dao;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

@Repository
public class ProductDao extends AbstractDao<ProductPojo>{

	ProductDao() {
		super(ProductPojo.class);
	}

	public ProductPojo getByBarcode(String barcode) throws ApiException{
		return selectByColumn("barcode", barcode);
	}

}
