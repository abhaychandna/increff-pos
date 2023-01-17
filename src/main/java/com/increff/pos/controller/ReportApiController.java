package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.InventoryReportData;
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
	@RequestMapping(path = "/reports/inventory", method = RequestMethod.GET)
	public List<InventoryReportData> inventoryReport() throws ApiException {
		return dto.inventoryReport();
	}

	@ApiOperation(value = "Sales Report")
	@RequestMapping(path = "/reports/sales", method = RequestMethod.GET)
	public List<SalesReportData> salesReport(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String brand, @RequestParam String category) throws ApiException {
		return dto.salesReport(startDate, endDate, brand, category);
	}


}
