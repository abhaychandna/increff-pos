package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/orders")
public class OrderApiController {

	@Autowired
	private OrderDto dto;

	@ApiOperation(value = "Gets a Order by ID")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public OrderData get(@PathVariable Integer id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets all Order")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PaginatedData<OrderData> getAll(@RequestParam Integer start, @RequestParam Integer length, @RequestParam Integer draw) throws ApiException {
		return dto.getAll(start, length, draw);
	}

}
