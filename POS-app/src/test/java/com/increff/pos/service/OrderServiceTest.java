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

public class OrderServiceTest extends AbstractUnitTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemDao orderItemDao;
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
        
        // QUES : Moving this in the init() method causes the test to fail
        testUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        barcode2 = "abcdef13";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createInventorySingle(barcode2, quantity);

        orderItemForm = new OrderItemForm(barcode, 1, 1.11);
        orderItemForm2 = new OrderItemForm(barcode2, 2, 2.22);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);
        orderItemPojoList = new ArrayList<OrderItemPojo>();
        orderItemFormList.forEach(orderItemForm -> {
            Integer productId = productDao.selectByColumn(ProductPojo.class, "barcode", orderItemForm.getBarcode()).getId();
            orderItemPojoList.add(new OrderItemPojo(productId, orderItemForm.getQuantity(), orderItemForm.getSellingPrice()));
        });
    }

    // testAdd
    @Test
    public void testAdd() throws ApiException {
        setup();
        orderService.add(orderItemPojoList);
        List<OrderItemPojo> orderItemPojoListGet = orderItemDao.selectByOrderId(orderItemPojoList.get(0).getOrderId());
        assertEquals(orderItemPojoList, orderItemPojoListGet);        
    }

    // testGet
    @Test
    public void testGet() throws ApiException {
        setup();
        orderService.add(orderItemPojoList);
        OrderPojo order = orderService.get(orderItemPojoList.get(0).getOrderId());
        assertEquals(orderItemPojoList.get(0).getOrderId(), order.getId());
    }

    // test filter by date
    @Test
    public void testFilterByDate() throws ApiException {
        setup();
        orderService.add(orderItemPojoList);
        OrderPojo order = orderService.get(orderItemPojoList.get(0).getOrderId());
        ZonedDateTime date = order.getTime();
        ZonedDateTime dateMinus1 = date.minusDays(1);
        ZonedDateTime datePlus1 = date.plusDays(1);
        List<OrderPojo> orderList = orderService.filterByDate(dateMinus1, datePlus1);
        assertEquals(1, orderList.size());

        orderService.add(orderItemPojoList);
        order = orderService.get(orderItemPojoList.get(0).getOrderId());
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
            orderService.get(id);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }

    @Test
    public void testGetCheckItemDoesNotExist() throws ApiException {
        Integer id = 1;
        String expectedMessage = "Order does not exist with id: " +  id;
        Exception exception = assertThrows(ApiException.class, () -> {
            orderService.get(id);
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
