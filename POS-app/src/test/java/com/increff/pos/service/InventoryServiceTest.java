package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;

public class InventoryServiceTest extends AbstractUnitTest {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private TestUtil testUtil;
    
    private static String brand;
    private static String category;
    private static String barcode;
    private static String name;
    private static Double mrp;
    private static Integer quantity;
    @Before
    public void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.0;
        quantity = 10;
        testUtil.createProductWithBrand(barcode, brand, category, name, mrp);
    }

    @Test
    public void testAddInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventorySingle(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        inventory = inventoryDao.select(InventoryPojo.class, inventory.getProductId());
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(quantity, inventory.getQuantity());
    }

    @Test
    public void testGetInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventorySingle(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        inventory = inventoryService.get(inventory.getProductId());
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(quantity, inventory.getQuantity());
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventorySingle(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer newQuantity = quantity + 5;
        inventory.setQuantity(newQuantity);
        inventoryService.update(inventory.getProductId(), inventory.getQuantity());
        inventory = inventoryService.get(inventory.getProductId());
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(newQuantity, inventory.getQuantity());
    }

    @Test
    public void testGetRecordsCount() throws ApiException {
        testUtil.createInventorySingle(barcode, quantity);
        testUtil.createInventory("barcode2", brand, category, name, mrp, quantity);
        testUtil.createInventory("barcode3", brand, category, name, mrp, quantity);
        Integer count = inventoryService.getRecordsCount();
        assertEquals(3, count);
    }

    @Test
    public void testAddInventoryExisting_thenIncreaseQuantity() throws ApiException {
        InventoryPojo inventory = testUtil.createInventorySingle(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer newQuantity = 15;
        InventoryPojo newInventory = new InventoryPojo(newQuantity);
        newInventory.setProductId(product.getId());
        inventoryService.add(newInventory);
        inventory = inventoryService.get(inventory.getProductId());
        Integer expectedQuantity = quantity + newQuantity;
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(expectedQuantity, inventory.getQuantity());
    }

    @Test
    public void testReduceInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventorySingle(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer reduce = 1;
        inventoryService.reduceInventory(product.getId(), reduce);
        inventory = inventoryService.get(inventory.getProductId());
        Integer expectedQuantity = quantity - reduce;
        assertEquals(expectedQuantity, inventory.getQuantity());
    }

    @Test
    public void testReduceInventoryInsufficientQuantity() throws ApiException {
        InventoryPojo inventory = testUtil.createInventorySingle(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer reduce = quantity+1;
        String expectedMessage = "Insufficient inventory for productId: " + product.getId() + ". Available: " + inventory.getQuantity() + ", Required: " + reduce;
        Exception exception = assertThrows(ApiException.class, () -> {
            inventoryService.reduceInventory(product.getId(), reduce);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetInventoryDoesNotExist() throws ApiException {
        String expectedMessage = "Inventory with given ID does not exist";
        Exception exception = assertThrows(ApiException.class, () -> {
            inventoryService.get(1);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }
    
}
