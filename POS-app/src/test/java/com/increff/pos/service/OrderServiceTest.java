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
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.util.TestUtil;

public class OrderServiceTest extends AbstractUnitTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemDao orderItemDao;
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
        orderItemFormList.forEach(orderItemForm -> {
            Integer productId = productDao.selectByColumn("barcode", orderItemForm.getBarcode()).getId();
            orderItemPojoList.add(new OrderItemPojo(productId, orderItemForm.getQuantity(), orderItemForm.getSellingPrice()));
        });
    }

    @Test
    public void testAdd() throws ApiException {
        orderService.add(orderItemPojoList);
        List<OrderItemPojo> orderItemPojoListGet = orderItemDao.selectByOrderId(orderItemPojoList.get(0).getOrderId());
        assertEquals(orderItemPojoList, orderItemPojoListGet);        
    }

    @Test
    public void testGetCheck() throws ApiException {
        orderService.add(orderItemPojoList);
        OrderPojo order = orderService.getCheck(orderItemPojoList.get(0).getOrderId());
        assertEquals(orderItemPojoList.get(0).getOrderId(), order.getId());
    }

    @Test
    public void testFilterByDate() throws ApiException {
        orderService.add(orderItemPojoList);
        OrderPojo order = orderService.getCheck(orderItemPojoList.get(0).getOrderId());
        ZonedDateTime date = order.getTime();
        ZonedDateTime dateMinus1 = date.minusDays(1);
        ZonedDateTime datePlus1 = date.plusDays(1);
        List<OrderPojo> orderList = orderService.filterByDate(dateMinus1, datePlus1);
        assertEquals(1, orderList.size());

        orderService.add(orderItemPojoList);
        order = orderService.getCheck(orderItemPojoList.get(0).getOrderId());
        ZonedDateTime nextDate = order.getTime();
        ZonedDateTime nextDateMinus1 = nextDate.minusDays(1);
        ZonedDateTime nextDatePlus1 = nextDate.plusDays(1);
        orderList = orderService.filterByDate(nextDateMinus1, nextDatePlus1);
        assertEquals(2, orderList.size());
    }



    @Test
    public void testGetCheckDoesNotExist() throws ApiException {
        Integer id = 1;
        String expectedMessage = "Order does not exist with id: " +  id;
        Exception exception = assertThrows(ApiException.class, () -> {
            orderService.getCheck(id);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetCheckItemDoesNotExist() throws ApiException {
        Integer id = 1;
        String expectedMessage = "Order does not exist with id: " +  id;
        Exception exception = assertThrows(ApiException.class, () -> {
            orderService.getCheck(id);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testAddEmptyOrder() throws ApiException {
        String expectedMessage = "No items in order";
        Exception exception = assertThrows(ApiException.class, () -> {
            orderService.add(new ArrayList<OrderItemPojo>());
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }
    
}
