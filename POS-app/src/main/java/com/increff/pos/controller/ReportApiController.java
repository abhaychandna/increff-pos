package com.increff.pos.controller;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.util.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/reports/")
public class ReportApiController {

	@Autowired
	private ReportDto dto;

	@ApiOperation(value = "Inventory Report")
	@RequestMapping(path = "inventory", method = RequestMethod.GET)
	public String inventoryReport() throws ApiException, FOPException, IOException, TransformerException {
		return dto.inventoryReport();
	}

	@ApiOperation(value = "Sales Report")
	@RequestMapping(path = "sales", method = RequestMethod.POST)
	public String salesReport(@RequestBody SalesReportForm form) throws ApiException {
		return dto.salesReport(form);
	}

	@ApiOperation(value = "Brand Report")
	@RequestMapping(path = "brand", method = RequestMethod.GET)
	public String brandReport() throws ApiException, FOPException, IOException, TransformerException {
		return dto.brandReport();
	}


}
