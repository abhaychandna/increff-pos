package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.DaySalesDto;
import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/daySaless")
public class DaySalesApiController {

	@Autowired
	private DaySalesDto dto;

	@ApiOperation(value = "Gets list of all daySaless")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PaginatedData<DaySalesData> getAll(@RequestParam Integer start, @RequestParam Integer length, @RequestParam Integer draw) throws ApiException {
		return dto.getAll(start, length, draw);
	}
}

