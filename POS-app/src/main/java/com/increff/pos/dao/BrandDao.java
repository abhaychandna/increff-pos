package com.increff.pos.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;
@Repository
public class BrandDao extends AbstractDao<BrandPojo>{

	BrandDao() {
		super(BrandPojo.class);
	}
	public List<BrandPojo> getByBrandCategory(String brand, String category) {
		return selectByMultipleColumns(
		List.of("brand", "category"),
		List.of(List.of(brand), List.of(category)));
	}	
}
