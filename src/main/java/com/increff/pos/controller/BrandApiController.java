package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api")
public class BrandApiController {

	@Autowired
	private BrandDto dto;

	@ApiOperation(value = "Adds a brand")
	@RequestMapping(path = "/brands", method = RequestMethod.POST)
	public void add(@RequestBody BrandForm f) throws ApiException {
		dto.add(f);
	}

	@ApiOperation(value = "Gets an brand by ID")
	@RequestMapping(path = "/brands/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable int id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets list of all brands")
	@RequestMapping(path = "/brands", method = RequestMethod.GET)
	public List<BrandData> getAll(@RequestParam Integer pageNo, @RequestParam Integer pageSize) throws ApiException {
		return dto.getAll(pageNo, pageSize);
	}

	@ApiOperation(value = "Updates an brand")
	@RequestMapping(path = "/brands/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		dto.update(id, f);
	}

}
