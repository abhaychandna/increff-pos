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
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/api/inventory")
public class InventoryApiController {

	@Autowired
	private InventoryDto dto;

	@ApiOperation(value = "Adds a Inventory")
	@RequestMapping(path = "", method = RequestMethod.POST)
	public void add(@RequestBody InventoryForm f) throws ApiException {
		dto.add(f);
	}

	@ApiOperation(value = "Adds multiple inventories in bulk")
	@RequestMapping(path = "/bulk-add", method = RequestMethod.POST)
	public void bulkAdd(@RequestBody List<InventoryForm> forms) throws ApiException, JsonProcessingException {
		dto.bulkAdd(forms);
	}

	@ApiOperation(value = "Gets a Inventory by ID")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public InventoryData get(@PathVariable Integer id) throws ApiException {
		return dto.get(id);
	}

	@ApiOperation(value = "Gets all Inventory")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PaginatedData<InventoryData> getAll(@RequestParam Integer start, @RequestParam Integer length, @RequestParam Integer draw) throws ApiException {
		return dto.getAll(start, length, draw);
	}

	@ApiOperation(value = "Gets Inventory by ID")
	@RequestMapping(path = "/barcode/{barcode}", method = RequestMethod.GET)
	public InventoryData getByBarcode(@PathVariable String barcode) throws ApiException {
		return dto.getByBarcode(barcode);
	}

	@RequestMapping(path = "", method = RequestMethod.PUT)
	public void update(@RequestBody InventoryForm f) throws ApiException {
		dto.update(f);
	}
}
