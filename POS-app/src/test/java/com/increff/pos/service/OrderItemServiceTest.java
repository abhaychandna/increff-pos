package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.increff.pos.util.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.util.TestUtil;

public class OrderItemServiceTest extends AbstractUnitTest {
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;
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
    private List<OrderItemPojo> orderItemPojoList;
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

        testUtil.createInventoryCascade(barcode, brand, category, name, mrp, quantity);
        barcode2 = "abcdef13";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createInventory(barcode2, quantity);

        orderItemForm = new OrderItemForm(barcode, quantity1, sellingPrice1);
        orderItemForm2 = new OrderItemForm(barcode2, quantity2, sellingPrice2);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);
        orderItemPojoList = new ArrayList<OrderItemPojo>();
        
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        orderDao.insert(order);

        orderItemFormList.forEach(orderItemForm -> {
            Integer productId = productDao.selectByColumn("barcode", orderItemForm.getBarcode()).getId();
            OrderItemPojo orderItem = new OrderItemPojo(productId, orderItemForm.getQuantity(), orderItemForm.getSellingPrice());
            orderItem.setOrderId(order.getId());
            orderItemPojoList.add(orderItem);
        });
    }

    @Test
    public void testAdd() throws ApiException {
        orderItemService.addItem(orderItemPojoList);
        Integer orderId = orderItemPojoList.get(0).getOrderId();
        List<OrderItemPojo> orderItemPojoListGet = orderItemDao.selectByColumn("orderId", List.of(orderId));
        assertEquals(orderItemPojoList, orderItemPojoListGet);
    }

    @Test
    public void testGetCheck() throws ApiException {
        orderItemService.addItem(orderItemPojoList);
        OrderItemPojo orderItemPojoListGet = orderItemService.getCheckItem(orderItemPojoList.get(0).getId());
        assertEquals(orderItemPojoList.get(0), orderItemPojoListGet);
    }

    @Test
    public void testGetByOrderId() throws ApiException {
        orderItemService.addItem(orderItemPojoList);
        Integer orderId = orderItemPojoList.get(0).getOrderId();
        List<OrderItemPojo> orderItemPojoListGet = orderItemService.getItemByOrderId(orderId);
        assertEquals(orderItemPojoList, orderItemPojoListGet);
    }

    @Test
    public void testGetCheckDoesNotExist() throws ApiException {
        Integer id = 1;
        String expectedMessage = "Order with given ID does not exist, id: " + id;
        Exception exception = assertThrows(ApiException.class, () -> {
            orderItemService.getCheckItem(id);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetByMultipleColumnDifferentColumnAndValueListSize() throws ApiException {
        String expectedMessage = "Column and value list size should be same";
        Exception exception = assertThrows(ApiException.class, () -> {
            orderItemService.getItemByMultipleColumns(List.of("orderId"), List.of(List.of(1),List.of(2)));
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }


    
}
