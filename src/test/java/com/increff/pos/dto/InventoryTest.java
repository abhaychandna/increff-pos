package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class InventoryTest extends AbstractUnitTest{
    
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private ProductDto productDto;

    @Test
    public void testAddInventory() throws ApiException{
        String barcode = "abcdef12";
        int quantity = 10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        int mrp = 100;
        InventoryData inventoryData = TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        ProductData productData = productDto.get(inventoryData.getId());
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(productData.getBarcode(), barcode);
    }

    @Test
    public void testAddInventoryBarcodeDoesntExist() throws ApiException{
        String barcode = "abcdef12";
        int quantity = 10;
        boolean error = false;
        try{
            InventoryData p = TestUtil.createInventorySingle(barcode, quantity);
        }
        catch(ApiException e){
            error = true;
        }
        assertTrue(error);
    }

    @Test
    public void testAddInventoryNegativeQuantity() throws ApiException {
        String barcode = "abcdef12";
        int quantity = -10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        int mrp = 100;
        
        boolean error = false;
        try{
            InventoryData p = TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        }
        catch(ApiException e){
            error = true;
        }
        assertTrue(error);
    }

    @Test
    public void testAddInventoryAlreadyExists() throws ApiException {
        String barcode = "abcdef12";
        int quantity = 10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        int mrp = 100;
        InventoryData p = TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        boolean error = false;
        try{
            InventoryData p1 = TestUtil.createInventorySingle(barcode, quantity);
        }
        catch(ApiException e){
            error = true;
        }
        assertTrue(error);
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        String barcode = "abcdef12";
        int quantity = 10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        int mrp = 100;
        InventoryData inventoryData = TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        ProductData productData = productDto.get(inventoryData.getId());
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(productData.getBarcode(), barcode);
        
        int newQuantity = 50;
        InventoryForm inventoryForm = TestUtil.getInventoryFormDto(barcode, newQuantity);
        inventoryDto.update(inventoryForm);
        InventoryData newData = inventoryDto.get(inventoryData.getId());
        assertEquals(newQuantity, newData.getQuantity());
    }

    @Test
    public void testUpdateInventoryDoesntExist() throws ApiException {
        String barcode = "abcdef12";
        int quantity = 10;
        boolean error = false;
        try{
            InventoryForm inventoryForm = TestUtil.getInventoryFormDto(barcode, quantity);
            inventoryDto.update(inventoryForm);
        }
        catch(ApiException e){
            error = true;
        }
        assertTrue(error);
    }

    @Test
    public void testGet() throws ApiException {
        String barcode = "abcdef12";
        int quantity = 10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        int mrp = 100;
        InventoryData inventoryData = TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        inventoryData = inventoryDto.get(inventoryData.getId());
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(barcode, inventoryData.getBarcode());
    }

}