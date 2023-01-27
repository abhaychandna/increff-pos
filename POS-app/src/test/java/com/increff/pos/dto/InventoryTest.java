package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.model.ProductData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class InventoryTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private ProductDto productDto;

    private static String brand;
    private static String category;
    private static String barcode;
    private static String name;
    private static Double mrp;
    private static Integer quantity;
    @BeforeClass
    public static void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.0;
        quantity = 10;
    }


    @Test
    public void testAddInventory() throws ApiException {
        TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        InventoryForm form = new InventoryForm(barcode, quantity);
        InventoryData inventoryData = inventoryDto.add(form);
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(inventoryData.getBarcode(), barcode);
    }

    @Test
    public void testAddInventoryBarcodeDoesntExist() throws ApiException {
        String barcode = "abcdef12";
        Integer quantity = 10;
        try {
            TestUtil.createInventorySingle(barcode, quantity);
        } catch (ApiException e) {
            return;
        }
        fail();
    }

    @Test(expected = ApiException.class)
    public void testAddInventoryNegativeQuantity() throws ApiException {
        Integer negativeQuantity = -10;
        TestUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        InventoryForm form = new InventoryForm(barcode, negativeQuantity);
        inventoryDto.add(form);
    }

    @Test
    public void testAddInventoryExisting_thenIncreaseQuantity() throws ApiException {

        TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);

        InventoryForm form = new InventoryForm(barcode, quantity);
        inventoryDto.add(form);

        PaginatedData<InventoryData> list = inventoryDto.getAll(0, 10, 1);
        assertEquals(1, list.getData().size());
        Integer newQuantity = 20;
        assertEquals(newQuantity, list.getData().get(0).getQuantity());
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        InventoryPojo inventoryPojo = TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        ProductData productData = productDto.get(inventoryPojo.getId());
        assertEquals(quantity, inventoryPojo.getQuantity());
        assertEquals(productData.getBarcode(), barcode);

        Integer newQuantity = 50;
        InventoryForm inventoryForm = TestUtil.getInventoryFormDto(barcode, newQuantity);
        inventoryDto.update(inventoryForm);
        InventoryData newData = inventoryDto.get(inventoryPojo.getId());
        assertEquals(newQuantity, newData.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testUpdateInventoryDoesntExist() throws ApiException {
        InventoryForm inventoryForm = TestUtil.getInventoryFormDto(barcode, quantity);
        inventoryDto.update(inventoryForm);
    }
    }

    @Test
    public void testGet() throws ApiException {
        String barcode = "abcdef12";
        Integer quantity = 10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        Double mrp = 100.0;
        InventoryData inventoryData = TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        inventoryData = inventoryDto.get(inventoryData.getId());
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(barcode, inventoryData.getBarcode());
    }
}