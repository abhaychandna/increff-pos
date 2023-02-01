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
    @Autowired
    private TestUtil testUtil;

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
        testUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        InventoryForm form = new InventoryForm(barcode, quantity);
        InventoryData inventoryData = inventoryDto.add(form);
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(inventoryData.getBarcode(), barcode);
    }

    @Test
    public void testNormalizeUppercaseToLowercase() throws ApiException {
        String expectedBarcode = "abcdef12";
        testUtil.createProductWithBrand(expectedBarcode, brand, category, name, mrp);
        barcode = "ABCDEF12";
        InventoryForm form = new InventoryForm(barcode, quantity);
        InventoryData inventoryData = inventoryDto.add(form);
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(inventoryData.getBarcode(), expectedBarcode);
    }
    
    @Test
    public void testNormalizeTrim() throws ApiException {
        String expectedBarcode = "abcdef12";
        testUtil.createProductWithBrand(expectedBarcode, brand, category, name, mrp);
        barcode = "  abcdef12  ";
        InventoryForm form = new InventoryForm(barcode, quantity);
        InventoryData inventoryData = inventoryDto.add(form);
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(inventoryData.getBarcode(), expectedBarcode);
    }

    @Test(expected = ApiException.class)
    public void testAddInventoryEmptyBarcode() throws ApiException {
        String emptyBarcode = "";
        InventoryForm form = new InventoryForm(emptyBarcode, quantity);
        inventoryDto.add(form);
    }

    @Test
    public void testAddInventoryBarcodeDoesntExist() throws ApiException {
        String barcode = "abcdef12";
        Integer quantity = 10;
        try {
            inventoryDto.add(new InventoryForm(barcode, quantity));
        } catch (ApiException e) {
            return;
        }
        fail();
    }

    @Test(expected = ApiException.class)
    public void testAddInventoryNegativeQuantity() throws ApiException {
        Integer negativeQuantity = -10;
        testUtil.createProductWithBrand(barcode, brand, category, name, mrp);
        InventoryForm form = new InventoryForm(barcode, negativeQuantity);
        inventoryDto.add(form);
    }

    @Test
    public void testAddInventoryExisting_thenIncreaseQuantity() throws ApiException {

        testUtil.createInventory(barcode, brand, category, name, mrp, quantity);

        InventoryForm form = new InventoryForm(barcode, quantity);
        inventoryDto.add(form);

        PaginatedData<InventoryData> list = inventoryDto.getAll(0, 10, 1);
        assertEquals(1, list.getData().size());
        Integer newQuantity = 20;
        assertEquals(newQuantity, list.getData().get(0).getQuantity());
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        InventoryPojo inventoryPojo = testUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        ProductData productData = productDto.get(inventoryPojo.getProductId());
        assertEquals(quantity, inventoryPojo.getQuantity());
        assertEquals(productData.getBarcode(), barcode);

        Integer newQuantity = 50;
        InventoryForm inventoryForm = testUtil.getInventoryFormDto(barcode, newQuantity);
        inventoryDto.update(inventoryForm);
        InventoryData newData = inventoryDto.get(inventoryPojo.getProductId());
        assertEquals(newQuantity, newData.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testUpdateInventoryDoesntExist() throws ApiException {
        InventoryForm inventoryForm = testUtil.getInventoryFormDto(barcode, quantity);
        inventoryDto.update(inventoryForm);
    }

    @Test
    public void testGet() throws ApiException {
        InventoryPojo inventory = testUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        InventoryData inventoryData = inventoryDto.get(inventory.getProductId());
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(barcode, inventoryData.getBarcode());
    }


    @Test(expected = ApiException.class)
    public void testGetDoesntExist() throws ApiException {
        inventoryDto.get(1);
    }

    @Test
    public void testGetAll() throws ApiException {
        testUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        testUtil.createInventory("abcdef13", brand, category, name, mrp, quantity);
        assertEquals(2, inventoryDto.getAll(0, 10, 1).getData().size());
    }

    @Test
    public void testGetByBarcode() throws ApiException {
        testUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        InventoryData inventoryData = inventoryDto.getByBarcode(barcode);
        assertEquals(quantity, inventoryData.getQuantity());
        assertEquals(barcode, inventoryData.getBarcode());
    }

    @Test
    public void testBulkAdd() throws ApiException {
        String barcode1 = "abcdef12", barcode2 = "abcdef13", barcode3 = "abcdef14";
        Integer quantity = 10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        Double mrp = 100.0;
        testUtil.createProductWithBrand(barcode1, brand, category, name, mrp);
        testUtil.createProductWithBrand(barcode2, brand, category, name, mrp);
        testUtil.createProductWithBrand(barcode3, brand, category, name, mrp);
        
        List<InventoryForm> inventoryForms = new ArrayList<>();
        inventoryForms.add(testUtil.getInventoryFormDto(barcode1, quantity));
        inventoryForms.add(testUtil.getInventoryFormDto(barcode2, quantity));
        inventoryForms.add(testUtil.getInventoryFormDto(barcode3, quantity));

        inventoryDto.bulkAdd(inventoryForms);
        assertEquals(3, inventoryDto.getAll(0, 10, 1).getData().size());
    }

    @Test
    public void testBulkAddExisting_thenIncreaseQuantity() throws ApiException {
        String barcode1 = "abcdef12", barcode2 = "abcdef13", barcode3 = "abcdef14";
        Integer quantity = 10;
        String brand = "adidas";
        String category = "tshirts";
        String name = "polo";
        Double mrp = 100.0;
        testUtil.createProductWithBrand(barcode1, brand, category, name, mrp);
        testUtil.createProductWithBrand(barcode2, brand, category, name, mrp);
        testUtil.createProductWithBrand(barcode3, brand, category, name, mrp);
        testUtil.createInventorySingle(barcode3, quantity);

        List<InventoryForm> inventoryForms = new ArrayList<>();
        inventoryForms.add(testUtil.getInventoryFormDto(barcode1, quantity));
        inventoryForms.add(testUtil.getInventoryFormDto(barcode2, quantity));
        inventoryForms.add(testUtil.getInventoryFormDto(barcode3, quantity));
        inventoryDto.bulkAdd(inventoryForms);

        List<InventoryData> inventoryData = inventoryDto.getAll(0, 10, 1).getData();
        assertEquals(3, inventoryData.size());
        for (InventoryData data : inventoryData) {
            if (data.getBarcode().equals(barcode3)) {
                Integer newQuantity = quantity + quantity;
                assertEquals(newQuantity, data.getQuantity());
            } else {
                assertEquals(quantity, data.getQuantity());
            }
        }
    }

    @Test()
    public void testBulkAddDuplicateInput_thenIncreaseQuantity() throws ApiException {

        testUtil.createProductWithBrand(barcode, brand, category, name, mrp);

        List<InventoryForm> inventoryForms = new ArrayList<>();
        inventoryForms.add(testUtil.getInventoryFormDto(barcode, quantity));
        inventoryForms.add(testUtil.getInventoryFormDto(barcode, quantity));
        inventoryDto.bulkAdd(inventoryForms);

        List<InventoryData> inventoryData = inventoryDto.getAll(0, 10, 1).getData();
        assertEquals(1, inventoryData.size());
        Integer newQuantity = quantity + quantity;
        assertEquals(newQuantity, inventoryData.get(0).getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testBulkAddBarcodeDoesntExist() throws ApiException {
        List<InventoryForm> inventoryForms = new ArrayList<>();
        inventoryForms.add(testUtil.getInventoryFormDto(barcode, quantity));
        inventoryDto.bulkAdd(inventoryForms);
    }


}