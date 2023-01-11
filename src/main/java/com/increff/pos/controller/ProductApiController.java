package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ProductApiController {

	@Autowired
	private ProductDto dto;
	
	@ApiOperation(value = "Adds a product")
	@RequestMapping(path = "/api/products", method = RequestMethod.POST)
	public void add(@RequestBody ProductForm f) throws ApiException {
		dto.add(f);
	}

	@ApiOperation(value = "Gets a product by ID")
	@RequestMapping(path = "/api/products/{id}", method = RequestMethod.GET)
	public ProductData get(@PathVariable int id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value="Gets all products")
	@RequestMapping(path = "/api/products", method = RequestMethod.GET)
	public List<ProductData> getAll() throws ApiException{
		return dto.getAll();
	}
 
	
	@RequestMapping(path = "/api/products/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException{
		dto.update(id, f);
	}




}
