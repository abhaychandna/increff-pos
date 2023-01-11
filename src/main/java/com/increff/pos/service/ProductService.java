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

	public void update(int id, ProductPojo p) throws ApiException{
		ProductPojo ex = getCheck(id);
		
		ProductSearchForm obj = new ProductSearchForm();
		obj.setBarcode(p.getBarcode());
		List<ProductPojo> products = dao.filter(obj);
		if(products.size() != 0){
			ProductPojo product = products.get(0);
			if(product.getId() != ex.getId())
				throw new ApiException("Product with barcode: " + p.getBarcode() + " already exists");
		}
		
		
		ex.setBarcode(p.getBarcode());
		ex.setName(p.getName());
		ex.setMrp(p.getMrp());
		ex.setBrandCategory(p.getBrandCategory());
		dao.update(ex);
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
