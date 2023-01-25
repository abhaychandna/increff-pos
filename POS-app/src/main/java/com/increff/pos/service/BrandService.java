package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandService {

	@Autowired
	private BrandDao dao;
	
	public BrandPojo add(BrandPojo brand) throws ApiException {
		List<BrandPojo> brands = dao.getByBrandCategory(brand.getBrand(), brand.getCategory());
		if (brands.size()>0)
			throw new ApiException("Brand Category pair already exists");
		dao.insert(brand);
		return brand;
	}

	
	public void bulkAdd(List<BrandPojo> brands) throws ApiException {
		brands.stream().forEach(e->dao.insert(e));
	} 

	public BrandPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	public List<BrandPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, BrandPojo.class);
	}

	public void update(Integer id, BrandPojo brand) throws ApiException {

		BrandPojo existing = getCheck(id);

		List<BrandPojo> brands = dao.getByBrandCategory(brand.getBrand(), brand.getCategory());
		if (brands.size()>0)
			throw new ApiException("Brand Category pair already exists");
		
		existing.setBrand(brand.getBrand());
		existing.setCategory(brand.getCategory());
		
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount(BrandPojo.class);
	}

	private BrandPojo getCheck(Integer id) throws ApiException {
		BrandPojo brand = dao.select(BrandPojo.class, id);
		if (Objects.isNull(brand)) {
			throw new ApiException("Brand with given ID does not exist, id: " + id);
		}
		return brand;
	}

	public BrandPojo getCheckBrandCategory(String brand, String category) throws ApiException {
		if (Objects.isNull(brand) || Objects.isNull(category)) {
			throw new ApiException("Brand or Category cannot be null");
		}
		List<BrandPojo> brands = dao.getByBrandCategory(brand, category);
		if (brands.size() == 0) {
			throw new ApiException("Brand Category pair does not exist");
		}
		return brands.get(0);
	}

	public <T> List<BrandPojo> getByColumn(String column, List<T> values){
		return dao.selectByColumn(BrandPojo.class, column, values);
	}
	public <T> List<BrandPojo> getByMultipleColumns(List<String> columns, List<List<T>> values){
		return dao.selectByMultipleColumns(BrandPojo.class, columns, values);
	}

	public List<BrandPojo> getAll() {
		return dao.selectAll(BrandPojo.class);
	}


}