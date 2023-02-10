package com.increff.pos.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.DaySalesDto;
import com.increff.pos.model.data.DaySalesData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.util.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/daySales")
public class DaySalesApiController {

	@Autowired
	private DaySalesDto dto;

	@ApiOperation(value = "Gets list of all daySales")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PaginatedData<DaySalesData> getAll(@RequestParam Integer start, @RequestParam Integer length, @RequestParam Integer draw, @RequestParam Optional<String> startDate, @RequestParam Optional<String> endDate) throws ApiException {
		String startDateParam = startDate.orElse("");
		String endDateParam = endDate.orElse("");
		return dto.getAll(start, length, draw, startDateParam, endDateParam);
	}
}


