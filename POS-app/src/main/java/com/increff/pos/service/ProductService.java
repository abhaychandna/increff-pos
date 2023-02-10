package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.increff.pos.util.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductService {

	@Autowired
	private ProductDao dao;

	public ProductPojo add(ProductPojo product) throws ApiException {
		ProductPojo existing = dao.selectByColumn("barcode", product.getBarcode());
		if(Objects.nonNull(existing)) {
			throw new ApiException("Product with barcode: " + product.getBarcode() + " already exists");
		}
		dao.insert(product);
		return product;
	}
	
	public void bulkAdd(List<ProductPojo> products) throws ApiException{
		for(ProductPojo product: products)add(product);
	}

	public List<ProductPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize);
	}

	public void update(Integer id, String name, Double mrp) throws ApiException {
		ProductPojo existing = getCheck(id);
		existing.setName(name);
		existing.setMrp(mrp);
	}

	public ProductPojo getCheck(Integer id) throws ApiException {
		ProductPojo product = dao.select(id);
		if (Objects.isNull(product)) {
			throw new ApiException("Product with id: " +  id + " does not exist");
		}
		return product;
	}
	
	public ProductPojo getCheckBarcode(String barcode) throws ApiException {
		ProductPojo product = dao.getByBarcode(barcode);
		if (Objects.isNull(product)) {
			throw new ApiException("Product with barcode: " + barcode + " does not exist");
		}
		return product;
	}

	public ProductPojo getByBarcode(String barcode) throws ApiException {
		return getCheckBarcode(barcode);
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount();
	}

	public <T> List<ProductPojo> getByColumn(String column, List<T> values){
		return dao.selectByColumn(column, values);
	}

}
