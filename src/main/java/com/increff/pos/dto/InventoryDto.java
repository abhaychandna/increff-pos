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
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;

@Component
public class InventoryDto {

    @Autowired
    InventoryService svc;

    public InventoryData add(InventoryForm inventoryForm) throws ApiException {
        InventoryPojo inventory = ConvertUtil.convert(inventoryForm);
        NormalizeUtil.normalize(inventory);
        ValidateUtil.validateInventory(inventory);
        try {
            svc.get(inventory.getId());
        } catch (ApiException e) {
            inventory = svc.add(inventory);
            return ConvertUtil.convert(inventory);
        }
        throw new ApiException("Inventory with given Barcode already exists");
    }

    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventory = svc.get(id);
        return ConvertUtil.convert(inventory);
    }

    public List<InventoryData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<InventoryPojo> brands = svc.getAll(pageNo, pageSize);
        List<InventoryData> respList = new ArrayList<InventoryData>();
        for (InventoryPojo p : brands) {
            respList.add(ConvertUtil.convert(p));
        }
        return respList;
    }

    public void update(InventoryForm f) throws ApiException {
        InventoryPojo inventory = ConvertUtil.convert(f);
        NormalizeUtil.normalize(inventory);
        ValidateUtil.validateInventory(inventory);
        svc.update(inventory.getId(), inventory);
    }
}
