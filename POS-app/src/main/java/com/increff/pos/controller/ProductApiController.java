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
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/products")
public class ProductApiController {

	@Autowired
	private ProductDto dto;

	@ApiOperation(value = "Adds a product")
	@RequestMapping(path = "", method = RequestMethod.POST)
	public void add(@RequestBody ProductForm f) throws ApiException {
		dto.add(f);
	}

	@ApiOperation(value = "Adds multiple products in bulk")
	@RequestMapping(path = "/add/bulk", method = RequestMethod.POST)
	public void bulkAdd(@RequestBody List<ProductForm> forms) throws ApiException, JsonProcessingException {
		dto.bulkAdd(forms);
	}

	@ApiOperation(value = "Gets a product by ID")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ProductData get(@PathVariable Integer id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets a product by barcode")
	@RequestMapping(path="", method = RequestMethod.GET, params = "barcode")
	public ProductData getByBarcode(@RequestParam String barcode) throws ApiException {
		return dto.getByBarcode(barcode);
	}

	@ApiOperation(value = "Gets all products")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PaginatedData<ProductData> getAll(@RequestParam Integer start, @RequestParam Integer length, @RequestParam Integer draw) throws ApiException {
		return dto.getAll(start, length, draw);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable Integer id, @RequestBody ProductForm f) throws ApiException {
		dto.update(id, f);
	}

}
