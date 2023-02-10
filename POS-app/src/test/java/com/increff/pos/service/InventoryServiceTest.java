package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.increff.pos.util.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.TestUtil;

public class InventoryServiceTest extends AbstractUnitTest {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private TestUtil testUtil;
    
    private String brand;
    private String category;
    private String barcode;
    private String name;
    private Double mrp;
    private Integer quantity;
    @Before
    public void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.0;
        quantity = 10;
        testUtil.createProductCascade(barcode, brand, category, name, mrp);
    }

    @Test
    public void testAddInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventory(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        inventory = inventoryDao.select(inventory.getProductId());
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(quantity, inventory.getQuantity());
    }

    @Test
    public void testGetCheckInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventory(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        inventory = inventoryService.getCheck(inventory.getProductId());
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(quantity, inventory.getQuantity());
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventory(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer newQuantity = quantity + 5;
        inventory.setQuantity(newQuantity);
        inventoryService.update(inventory.getProductId(), inventory.getQuantity());
        inventory = inventoryService.getCheck(inventory.getProductId());
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(newQuantity, inventory.getQuantity());
    }

    @Test
    public void testGetRecordsCount() throws ApiException {
        testUtil.createInventory(barcode, quantity);
        String barcode2 = "barcode2", barcode3 = "barcode3";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createProduct(barcode3, brand, category, name, mrp);
        testUtil.createInventory(barcode2, quantity);
        testUtil.createInventory(barcode3, quantity);
        Integer count = inventoryService.getRecordsCount();
        assertEquals(3, count);
    }

    @Test
    public void testAddInventoryExisting_thenIncreaseQuantity() throws ApiException {
        InventoryPojo inventory = testUtil.createInventory(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer newQuantity = 15;
        InventoryPojo newInventory = new InventoryPojo(newQuantity);
        newInventory.setProductId(product.getId());
        inventoryService.add(newInventory);
        inventory = inventoryService.getCheck(inventory.getProductId());
        Integer expectedQuantity = quantity + newQuantity;
        assertEquals(product.getId(), inventory.getProductId());
        assertEquals(expectedQuantity, inventory.getQuantity());
    }

    @Test
    public void testReduceInventory() throws ApiException {
        InventoryPojo inventory = testUtil.createInventory(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer reduce = 1;
        inventoryService.reduceInventory(product.getId(), reduce);
        inventory = inventoryService.getCheck(inventory.getProductId());
        Integer expectedQuantity = quantity - reduce;
        assertEquals(expectedQuantity, inventory.getQuantity());
    }

    @Test
    public void testReduceInventoryInsufficientQuantity() throws ApiException {
        InventoryPojo inventory = testUtil.createInventory(barcode, quantity);
        ProductPojo product = productService.getByBarcode(barcode);
        Integer reduce = quantity+1;
        String expectedMessage = "Insufficient inventory for productId: " + product.getId() + ". Available: " + inventory.getQuantity() + ", Required: " + reduce;
        Exception exception = assertThrows(ApiException.class, () -> {
            inventoryService.reduceInventory(product.getId(), reduce);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetCheckInventoryDoesNotExist() throws ApiException {
        String expectedMessage = "Inventory does not exist";
        Exception exception = assertThrows(ApiException.class, () -> {
            inventoryService.getCheck(1);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }
    
}
