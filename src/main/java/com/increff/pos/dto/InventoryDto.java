package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService svc;
    @Autowired
    private ProductService productService;

    public InventoryData add(InventoryForm inventoryForm) throws ApiException {
        InventoryPojo inventory = convert(inventoryForm);
        NormalizeUtil.normalize(inventory);
        ValidateUtil.validateInventory(inventory);
        return convert(svc.add(inventory));
    }

    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventory = svc.get(id);
        return convert(inventory);
    }

    public List<InventoryData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<InventoryPojo> brands = svc.getAll(pageNo, pageSize);
        List<InventoryData> respList = new ArrayList<InventoryData>();
        for (InventoryPojo p : brands) {
            respList.add(convert(p));
        }
        return respList;
    }

    public void update(InventoryForm f) throws ApiException {
        InventoryPojo inventory = convert(f);
        NormalizeUtil.normalize(inventory);
        ValidateUtil.validateInventory(inventory);
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
