package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.InventoryFormErrorData;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PreProcessingUtil;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService svc;
    @Autowired
    private ProductService productService;

    public InventoryData add(InventoryForm inventoryForm) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(inventoryForm);
        InventoryPojo inventory = convert(inventoryForm);
        return convert(svc.add(inventory));
    }

    public void bulkAdd(List<InventoryForm> inventoryForms) throws ApiException {
        bulkAddValidate(inventoryForms);

        List<InventoryPojo> validInventories = new ArrayList<InventoryPojo>();
        List<InventoryFormErrorData> errors = new ArrayList<InventoryFormErrorData>();
        for (InventoryForm form : inventoryForms) {
            try {
                InventoryPojo inventory = convert(form);
                validInventories.add(inventory);
            }
            catch (ApiException e) {
                errors.add(new InventoryFormErrorData(form.getBarcode(), form.getQuantity(), e.getMessage()));
            }
        }
        if(errors.size() > 0 ) throwErrors(errors);
        
        svc.bulkAdd(validInventories);   
    }

    private void bulkAddValidate(List<InventoryForm> forms) throws ApiException {
        List<InventoryFormErrorData> errors = new ArrayList<InventoryFormErrorData>();
        forms.forEach(form->{
            try {
                PreProcessingUtil.normalizeAndValidate(form);
            } catch (ApiException e) {
                errors.add(new InventoryFormErrorData(form.getBarcode(), form.getQuantity(), e.getMessage()));
            }
        });
        if(errors.size() > 0 ) throwErrors(errors);
    }

    private void throwErrors(List<InventoryFormErrorData> errors) throws ApiException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
            throw new ApiException(json);
        }
        catch (JsonProcessingException e) {
            throw new ApiException("Error in parsing error data to json");
        }
    }

    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventory = svc.get(id);
        return convert(inventory);
    }

    public InventoryData getByBarcode(String barcode) throws ApiException {
        InventoryPojo inventory = svc.get(productService.getByBarcode(barcode).getId());
        return convert(inventory);
    }

    public PaginatedData<InventoryData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<InventoryPojo> inventories = svc.getAll(pageNo, pageSize);
        List<InventoryData> inventoryDatas = new ArrayList<InventoryData>();
        for (InventoryPojo p : inventories) {
            inventoryDatas.add(convert(p));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<InventoryData>(inventoryDatas, draw, count, count);
    }

    public void update(InventoryForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        InventoryPojo inventory = convert(form);
        svc.update(inventory.getId(), form.getQuantity());
    }

    private InventoryPojo convert(InventoryForm inventoryForm) throws ApiException {
        InventoryPojo inventory = ConvertUtil.convert(inventoryForm, InventoryPojo.class);
        inventory.setId(productService.getByBarcode(inventoryForm.getBarcode()).getId());
        return inventory;
    }

    private InventoryData convert(InventoryPojo inventoryPojo) throws ApiException {
        InventoryData inventory = ConvertUtil.convert(inventoryPojo, InventoryData.class);
        inventory.setBarcode(productService.get(inventoryPojo.getId()).getBarcode());
        return inventory;
    }
}
