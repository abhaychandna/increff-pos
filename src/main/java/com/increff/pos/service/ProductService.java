package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

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
		checkBarcodeDoesntExist(product.getBarcode());
		dao.insert(product);
		return product;
	}

	public ProductPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	public List<ProductPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, ProductPojo.class);
	}

	public void update(int id, ProductPojo product) throws ApiException {
		ProductPojo existing = getCheck(id);

		// Check if barcode already exists for another product
		ProductPojo productBarcode = null;
		try {
			productBarcode = dao.getByBarcode(product.getBarcode());
		} catch (ApiException e) {
			// do nothing
		}
		if (Objects.nonNull(productBarcode) && productBarcode.getId() != id)
			throw new ApiException(
					"Product with barcode: " + product.getBarcode() + " already exists for another product");

		existing.setBarcode(product.getBarcode());
		existing.setName(product.getName());
		existing.setMrp(product.getMrp());
		existing.setBrandCategory(product.getBrandCategory());
		dao.update(existing);
	}

	private ProductPojo getCheck(int id) throws ApiException {
		ProductPojo product = dao.select(ProductPojo.class, id);
		if (Objects.isNull(product)) {
			throw new ApiException("Brand with given ID does not exist, id: " + id);
		}
		return product;
	}

	public ProductPojo getByBarcode(String barcode) throws ApiException {
		return dao.getByBarcode(barcode);
	}

	private void checkBarcodeDoesntExist(String barcode) throws ApiException {
		try {
			dao.getByBarcode(barcode);
		} catch (ApiException e) {
			return;
		}
		// throw exception as barcode already exists
		throw new ApiException("Product with barcode: " + barcode + " already exists");

	}

}
