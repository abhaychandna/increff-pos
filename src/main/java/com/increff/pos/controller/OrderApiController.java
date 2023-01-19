package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/orders")
public class OrderApiController {

	@Autowired
	private OrderDto orderDto;

	@ApiOperation(value = "Adds a Order")
	@RequestMapping(path = "", method = RequestMethod.POST)
	public void add(@RequestBody List<OrderItemForm> forms) throws ApiException {
		orderDto.add(forms);
	}

	@ApiOperation(value = "Gets a OrderItem by ID")
	@RequestMapping(path = "/items/{id}", method = RequestMethod.GET)
	public OrderItemData getItem(@PathVariable Integer id) throws ApiException {
		return orderDto.getItem(id);
	}

	@ApiOperation(value = "Gets a Order by ID")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public OrderData get(@PathVariable Integer id) throws ApiException {
		return orderDto.get(id);
	}

	@ApiOperation(value = "Gets all Order")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PaginatedData<OrderData> getAll(@RequestParam Integer start, @RequestParam Integer length, @RequestParam Integer draw) throws ApiException {
		return orderDto.getAll(start, length, draw);
	}

}
