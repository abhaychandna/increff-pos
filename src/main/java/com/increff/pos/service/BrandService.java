package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;

@Service
public class BrandService {

	@Autowired
	private BrandDao dao;
	
	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo add(BrandPojo p) throws ApiException {
		List<BrandPojo> brands = dao.getByBrandCategory(p.getBrand(), p.getCategory());
		if (brands.size()>0)
			throw new ApiException("Brand Category pair already exists");
		dao.insert(p);
		return p;
	}

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	@Transactional
	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public void update(int id, BrandPojo p) throws ApiException {

		BrandPojo ex = getCheck(id);

		List<BrandPojo> brands = dao.getByBrandCategory(p.getBrand(), p.getCategory());
		if (brands.size()>0)
			throw new ApiException("Brand Category pair already exists");
		
		ex.setBrand(p.getBrand());
		ex.setCategory(p.getCategory());
		dao.update(ex);
	}

	@Transactional
	private BrandPojo getCheck(int id) throws ApiException {
		BrandPojo p = dao.select(BrandPojo.class, id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exist, id: " + id);
		}
		return p;
	}

	public BrandPojo getCheckBrandCategory(String brand, String category) throws ApiException {
		List<BrandPojo> brands = dao.getByBrandCategory(brand, category);
		if (brands.size() == 0) {
			throw new ApiException("Brand Category pair does not exist");
		}
		return brands.get(0);
	}


}
