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
import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.BrandBulkAddData;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.BrandSearchData;
import com.increff.pos.model.SalesReportData;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api")
public class ReportApiController {

	@Autowired
	private ReportDto dto;

	@ApiOperation(value = "Inventory Report")
	@RequestMapping(path = "/reports/inventory", method = RequestMethod.POST)
	public List<SalesReportData> inventoryReport() throws ApiException {
		return dto.inventoryReport();
	}

}
