package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;

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

    private static String brand;
    private static String category;
    private static String barcode;
    private static String name;
    private static Double mrp;
    private static Integer quantity;
    private static String barcode2; 
    private static OrderItemForm orderItemForm;
    private static OrderItemForm orderItemForm2;
    private static List<OrderItemForm> orderItemFormList;
    private static List<OrderItemPojo> orderItemPojoList;
    private static Double sellingPrice;
    @BeforeClass
    public static void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.0;
        quantity = 10;
        sellingPrice = 1.11;
    }

    private void setup() throws ApiException{
        testUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        barcode2 = "abcdef13";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createInventorySingle(barcode2, quantity);

        orderItemForm = new OrderItemForm(barcode, 1, 1.11);
        orderItemForm2 = new OrderItemForm(barcode2, 2, 2.22);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);
        orderItemPojoList = new ArrayList<OrderItemPojo>();
        
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        orderDao.insert(order);

        orderItemFormList.forEach(orderItemForm -> {
            Integer productId = productDao.selectByColumn(ProductPojo.class, "barcode", orderItemForm.getBarcode()).getId();
            OrderItemPojo orderItem = new OrderItemPojo(productId, orderItemForm.getQuantity(), orderItemForm.getSellingPrice());
            orderItem.setOrderId(order.getId());
            orderItemPojoList.add(orderItem);
        });
    }

    @Test
    public void testAdd() throws ApiException {
        setup();
        orderItemService.add(orderItemPojoList);
        Integer orderId = orderItemPojoList.get(0).getOrderId();
        List<OrderItemPojo> orderItemPojoListGet = orderItemDao.selectByColumn(OrderItemPojo.class, "orderId", List.of(orderId));
        assertEquals(orderItemPojoList, orderItemPojoListGet);
    }

    @Test
    public void testGet() throws ApiException {
        setup();
        orderItemService.add(orderItemPojoList);
        OrderItemPojo orderItemPojoListGet = orderItemService.get(orderItemPojoList.get(0).getId());
        assertEquals(orderItemPojoList.get(0), orderItemPojoListGet);
    }

    @Test
    public void testGetByOrderId() throws ApiException {
        setup();
        orderItemService.add(orderItemPojoList);
        Integer orderId = orderItemPojoList.get(0).getOrderId();
        List<OrderItemPojo> orderItemPojoListGet = orderItemService.getByOrderId(orderId);
        assertEquals(orderItemPojoList, orderItemPojoListGet);
    }

    @Test
    public void testGetCheckDoesNotExist() throws ApiException {
        Integer id = 1;
        String expectedMessage = "Order with given ID does not exist, id: " + id;
        Exception exception = assertThrows(ApiException.class, () -> {
            orderItemService.get(id);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }


    
}
