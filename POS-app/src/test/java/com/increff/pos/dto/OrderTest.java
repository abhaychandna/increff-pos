package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class OrderTest extends AbstractUnitTest {

    @Autowired
    private OrderDto orderDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private TestUtil testUtil;

    private String brand;
    private String category;
    private String barcode;
    private String name;
    private Double mrp;
    private Integer quantity;
    private String barcode2; 
    private OrderItemForm orderItemForm;
    private OrderItemForm orderItemForm2;
    private List<OrderItemForm> orderItemFormList;
    private Double sellingPrice1;
    private Integer quantity1;
    private Double sellingPrice2;
    private Integer quantity2;
    @Before
    public void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.0;
        quantity = 10;
        quantity1 = 1;
        quantity2 = 2;
        sellingPrice1 = 1.11;
        sellingPrice2 = 2.22;
        testUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        barcode2 = "abcdef13";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createInventorySingle(barcode2, quantity);

        orderItemForm = new OrderItemForm(barcode, quantity1, sellingPrice1);
        orderItemForm2 = new OrderItemForm(barcode2, quantity2, sellingPrice2);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);
    }

    @Test
    public void testAddOrder() throws ApiException {
        List<OrderItemData> orderItems = orderDto.add(orderItemFormList);
        checkEquals(orderItems, orderItemFormList);
    }

    @Test
    public void testGetOrder() throws ApiException {
        List<OrderItemPojo> orderItems = testUtil.createOrder(orderItemFormList);
        OrderData orderData = orderDto.get(orderItems.get(0).getOrderId());
        assertEquals(orderData.getId(), orderItems.get(0).getOrderId());
    }

    @Test
    public void testGetOrderItem() throws ApiException {
        List<OrderItemPojo> orderItems = testUtil.createOrder(orderItemFormList);
        OrderItemPojo orderItem = orderItems.get(0);
        OrderItemData orderItemData = orderDto.getItem(orderItem.getId());
        checkEquals(orderItemData, orderItemForm);
    }

    @Test
    public void testGetItemsByOrderId() throws ApiException {
        testUtil.createOrder(orderItemFormList);
        List<OrderItemData> orderItemDataList = orderDto.getItemsByOrderId(1);
        checkEquals(orderItemDataList, orderItemFormList);
    }

    @Test
    public void testGetAllItems() throws ApiException {
        testUtil.createOrder(orderItemFormList);
        List<OrderItemData> orderItemDataList = orderDto.getAllItems(0, 10, 1).getData();
        assertEquals(orderItemFormList.size(), orderItemDataList.size());
    }

    @Test
    public void testGetAll() throws ApiException {
        Integer orderCount = 3;
        for (Integer i = 0; i < orderCount; i++) {
            testUtil.createOrder(orderItemFormList);
        }
        List<OrderData> orderDataList = orderDto.getAll(0, 10, 1).getData();
        assertEquals(orderCount, Integer.valueOf(orderDataList.size()));
    }

    @Test
    public void testGetInvoice() throws ApiException {
        List<OrderItemPojo> orderItems =  testUtil.createOrder(orderItemFormList);
        Integer orderId = orderItems.get(0).getOrderId();
        String base64 = orderDto.getInvoice(orderId);
        assertNotNull(base64);
    }

    @Test(expected = ApiException.class)
    public void testDuplicateBarcode() throws ApiException {
        orderItemForm.setBarcode(barcode2);
        orderItemForm2.setBarcode(barcode2);
        orderDto.add(List.of(orderItemForm, orderItemForm2));
    }

    @Test
    public void testInventoryQuantityReducedAfterPlaceOrder() throws ApiException {
        Integer originalQuantity = inventoryDto.getByBarcode(barcode).getQuantity();
        Integer barcodeQuantity = orderItemFormList.stream().filter(orderItemForm -> orderItemForm.getBarcode().equals(barcode)).findFirst().get().getQuantity();
        Integer finalQuantityExpected = originalQuantity - barcodeQuantity;
        
        orderDto.add(orderItemFormList);
        Integer finalQuantity = inventoryDto.getByBarcode(barcode).getQuantity();
        assertEquals(finalQuantityExpected, finalQuantity);
    }

    @Test(expected = ApiException.class)
    public void testInsufficientInventory() throws ApiException {
        Integer originalQuantity = inventoryDto.getByBarcode(barcode).getQuantity();
        orderItemForm.setQuantity(originalQuantity + 1);
        orderDto.add(List.of(orderItemForm));
    }

    private void checkEquals(List<OrderItemData> orderItems, List<OrderItemForm> orderItemFormList) {
        for (int i = 0; i < orderItems.size(); i++) {
            assertEquals(orderItems.get(i).getBarcode(), orderItemFormList.get(i).getBarcode());
            assertEquals(orderItems.get(i).getQuantity(), orderItemFormList.get(i).getQuantity());
            assertEquals(orderItems.get(i).getSellingPrice(), orderItemFormList.get(i).getSellingPrice());
        }
    }
    private void checkEquals(OrderItemData orderItemData, OrderItemForm orderItemForm) {
        checkEquals(List.of(orderItemData), List.of(orderItemForm));
    }

}