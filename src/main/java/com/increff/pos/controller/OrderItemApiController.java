package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.OrderItemDto;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemPutForm;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api")
public class OrderItemApiController {

	@Autowired
	private OrderItemDto dto;

	@ApiOperation(value = "Adds a OrderItem")
	@RequestMapping(path = "/order-items", method = RequestMethod.POST)
	public void add(@RequestBody List<OrderItemForm> forms) throws ApiException {
		dto.add(forms);
	}

	@ApiOperation(value = "Gets a OrderItem by ID")
	@RequestMapping(path = "/order-items/{id}", method = RequestMethod.GET)
	public OrderItemData get(@PathVariable Integer id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets all OrderItems")
	@RequestMapping(path = "/order-items", method = RequestMethod.GET)
	public List<OrderItemData> getAll(@RequestParam Integer pageNo, @RequestParam Integer pageSize) throws ApiException {
		return dto.getAll(pageNo, pageSize);
	}

	@RequestMapping(path = "/order-items/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable Integer id, @RequestBody OrderItemPutForm f) throws ApiException {
		dto.update(id, f);
	}
}
