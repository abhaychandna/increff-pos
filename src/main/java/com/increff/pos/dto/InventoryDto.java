package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
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

    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventory = svc.get(id);
        return convert(inventory);
    }

    public PaginatedData<InventoryData> getAll(Integer start, Integer length, Integer draw) throws ApiException {
        Integer pageNo = start/length;
        Integer pageSize = length;
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
        svc.update(inventory.getId(), inventory);
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
