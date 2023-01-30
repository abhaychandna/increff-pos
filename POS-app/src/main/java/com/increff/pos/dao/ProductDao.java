package com.increff.pos.dao;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

@Repository
public class ProductDao extends AbstractDao{

	public ProductPojo getByBarcode(String barcode) throws ApiException{
		return selectByColumn(ProductPojo.class, "barcode", barcode);
	}

}
