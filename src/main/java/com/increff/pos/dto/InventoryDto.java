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
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;


@Component
public class InventoryDto {
    
    @Autowired
    InventoryService svc;
    
    public InventoryPojo add(InventoryForm f) throws ApiException {
        InventoryPojo p = ConvertUtil.convert(f);        
        NormalizeUtil.normalize(p);
		ValidateUtil.validateInventory(p);
        try{
            svc.get(p.getId());
        }catch(ApiException e){
            return svc.add(p);
        }
        throw new ApiException("Inventory with given Barcode already exists");
    }

    public InventoryData get(int id) throws ApiException {
        InventoryPojo p = svc.get(id);
		return ConvertUtil.convert(p);
    }

    public List<InventoryData> getAll() throws ApiException{
        List<InventoryPojo> brands = svc.getAll();
        List<InventoryData> respList = new ArrayList<InventoryData>();
        for (InventoryPojo p : brands) {
            respList.add(ConvertUtil.convert(p));
        }
        return respList;
    }

    public void update(InventoryForm f) throws ApiException {
        InventoryPojo p = ConvertUtil.convert(f);
        NormalizeUtil.normalize(p);
		ValidateUtil.validateInventory(p);
        svc.update(p.getId(), p);
    }
}
