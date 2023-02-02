package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/brands")
public class BrandApiController {

	@Autowired
	private BrandDto dto;

	@ApiOperation(value = "Adds a brand")
	@RequestMapping(path = "", method = RequestMethod.POST)
	public void add(@RequestBody BrandForm f) throws ApiException {
		dto.add(f);
	}

	@ApiOperation(value = "Adds multiple brands in bulk")
	@RequestMapping(path = "/bulk-add", method = RequestMethod.POST)
	public void bulkAdd(@RequestBody List<BrandForm> forms) throws ApiException, JsonProcessingException {
		dto.bulkAdd(forms);
	}

	@ApiOperation(value = "Gets an brand by ID")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable Integer id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets list of all brands")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PaginatedData<BrandData> getAll(@RequestParam Integer start, @RequestParam Integer length, @RequestParam Integer draw) throws ApiException {
		return dto.getAll(start, length, draw);
	}

	@ApiOperation(value = "Updates an brand")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable Integer id, @RequestBody BrandForm f) throws ApiException {
		dto.update(id, f);
	}

}
