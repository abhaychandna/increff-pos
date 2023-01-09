package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.StringUtil;

@Service
public class ProductService {

	@Autowired
	private ProductDao dao;
	@Autowired
	private BrandDao bdao;

	@Transactional(rollbackOn = ApiException.class)
	public ProductPojo add(ProductPojo p) throws ApiException{
		validate(p);
		dao.insert(p);
		return p;
	}

	@Transactional
	public ProductPojo get(int id) throws ApiException{
		return getCheck(id);
	}
	@Transactional
	public List<ProductPojo> getAll(){
		return dao.selectAll();
	}

	@Transactional(rollbackOn = ApiException.class)
	public void update(int id, ProductPojo p) throws ApiException{
		ProductPojo ex = getCheck(id);
		
		ProductPojo obj = new ProductPojo();
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
		ex.setBrand_category(p.getBrand_category());
		dao.update(ex);
	}

	@Transactional
	private ProductPojo getCheck(int id) throws ApiException{
		ProductPojo p = new ProductPojo();
		p.setId(id);
		List<ProductPojo> products = dao.filter(p);
		if(products.size() == 0)
			throw new ApiException("Product does not exist with id: " + id);
		return products.get(0);
	}

	
	@Transactional
    public ProductPojo getByBarcode(String barcode) throws ApiException{
		ProductPojo p = new ProductPojo();
		p.setBarcode(barcode);
		List<ProductPojo> products = dao.filter(p);
		if(products.size() == 0)
			throw new ApiException("Product does not exist with barcode: " + barcode);
		return products.get(0);
	}

	// QUES : Where to put database validations ?? Is this OK ?? 
	@Transactional
	private void validate(ProductPojo p) throws ApiException{
		ProductPojo product = new ProductPojo();
		product.setBarcode(p.getBarcode());
		if(dao.filter(product).size() == 1)
			throw new ApiException("Product with barcode: " + p.getBarcode() + " already exists");
	}
 	
}
