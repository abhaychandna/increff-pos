package com.increff.pos.dto;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.InventoryFormErrorData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.ErrorUtil;
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
        List<InventoryPojo> validInventories = convertBulk(inventoryForms);
        svc.bulkAdd(validInventories);   
    }

    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventory = svc.getCheck(id);
        return convert(inventory);
    }

    public InventoryData getByBarcode(String barcode) throws ApiException {
        InventoryPojo inventory = svc.getCheck(productService.getByBarcode(barcode).getId());
        return convert(inventory);
    }

    public PaginatedData<InventoryData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<InventoryPojo> inventories = svc.getAll(pageNo, pageSize);
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
        for (InventoryPojo p : inventories) {
            inventoryDataList.add(convert(p));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<InventoryData>(inventoryDataList, draw, count, count);
    }

    public void update(InventoryForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        InventoryPojo inventory = convert(form);
        svc.update(inventory.getProductId(), form.getQuantity());
    }

    private List<InventoryPojo> convertBulk(List<InventoryForm> forms) throws ApiException {
        List<String> barcodeList = forms.stream().map(InventoryForm::getBarcode).collect(Collectors.toList());
        List<ProductPojo> products = productService.getByColumn("barcode", barcodeList);
        HashMap<String, Integer> barcodeToId = new HashMap<String, Integer>();
        for (ProductPojo p : products) {
            barcodeToId.put(p.getBarcode(), p.getId());
        }

        List<InventoryPojo> validInventories = new ArrayList<InventoryPojo>();
        List<InventoryFormErrorData> errors = new ArrayList<InventoryFormErrorData>();
        Boolean errorFound = false;
        for (InventoryForm form : forms){
            InventoryPojo inventory = ConvertUtil.convert(form, InventoryPojo.class);
            Integer productId = barcodeToId.get(form.getBarcode());
            if(Objects.nonNull(productId)) {
                inventory.setProductId(productId);
                validInventories.add(inventory);
                errors.add(new InventoryFormErrorData(form.getBarcode(), form.getQuantity(), ""));
            }
            else{
                errorFound = true;
                errors.add(new InventoryFormErrorData(form.getBarcode(), form.getQuantity(), "Barcode does not exist"));
            }
        }
        if(errorFound) ErrorUtil.throwErrors(errors);
        return validInventories;
    }

    private void bulkAddValidate(List<InventoryForm> forms) throws ApiException {
        checkDuplicateBarcodes(forms);
    }


    private void checkDuplicateBarcodes(List<InventoryForm> forms) throws ApiException {
        List<InventoryFormErrorData> errors = new ArrayList<InventoryFormErrorData>();
        Boolean errorFound = false;
        Set<String> barcodeSet = new HashSet<String>();
        for (InventoryForm form : forms) {
            try {
                PreProcessingUtil.normalizeAndValidate(form);
                if (barcodeSet.contains(form.getBarcode())) throw new ApiException("Duplicate Barcodes not allowed");
                barcodeSet.add(form.getBarcode());
                errors.add(new InventoryFormErrorData(form.getBarcode(), form.getQuantity(), ""));
            } catch (ApiException e) {
                errorFound = true;
                errors.add(new InventoryFormErrorData(form.getBarcode(), form.getQuantity(), e.getMessage()));
            }
        }

        if(errorFound) ErrorUtil.throwErrors(errors);
    }

    private InventoryPojo convert(InventoryForm inventoryForm) throws ApiException {
        InventoryPojo inventory = ConvertUtil.convert(inventoryForm, InventoryPojo.class);
        inventory.setProductId(productService.getByBarcode(inventoryForm.getBarcode()).getId());
        return inventory;
    }

    private InventoryData convert(InventoryPojo inventoryPojo) throws ApiException {
        InventoryData inventory = ConvertUtil.convert(inventoryPojo, InventoryData.class);
        inventory.setBarcode(productService.getCheck(inventoryPojo.getProductId()).getBarcode());
        return inventory;
    }
}
