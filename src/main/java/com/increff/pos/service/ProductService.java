package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.ProductSearchForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.StringUtil;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductService {

	@Autowired
	private ProductDao dao;

	public ProductPojo add(ProductPojo p) throws ApiException{
		checkBarcodeDoesntExist(p);
		dao.insert(p);
		return p;
	}

	public ProductPojo get(int id) throws ApiException{
		return getCheck(id);
	}
	public List<ProductPojo> getAll(){
		return dao.selectAll();
	}

	public void update(int id, ProductPojo product) throws ApiException{
		ProductPojo existing = getCheck(id);
		
		// Check if barcode already exists for another product
		ProductPojo productBarcode = null;
		try{
			productBarcode = getByBarcode(product.getBarcode());	
		}
		catch(ApiException e){
			// do nothing
		}
		if(Objects.nonNull(productBarcode) && productBarcode.getId() != id)
			throw new ApiException("Product with barcode: " + product.getBarcode() + " already exists for another product");
		
		existing.setBarcode(product.getBarcode());
		existing.setName(product.getName());
		existing.setMrp(product.getMrp());
		existing.setBrandCategory(product.getBrandCategory());
		dao.update(existing);
	}

	private ProductPojo getCheck(int id) throws ApiException{
		return dao.select(ProductPojo.class, id);
	}

	
    public ProductPojo getByBarcode(String barcode) throws ApiException{
		return dao.getByBarcode(barcode);
	}
 
	private void checkBarcodeDoesntExist(ProductPojo p) throws ApiException{
		try{
			ProductPojo exists = dao.getByBarcode(p.getBarcode()); 
		}
		catch(ApiException e){
			return;
		}
		// throw exception as barcode already exists 
		throw new ApiException("Product with barcode: " + p.getBarcode() + " already exists");

	}
 	
}
