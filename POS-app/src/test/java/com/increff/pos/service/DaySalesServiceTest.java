package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;

public class DaySalesServiceTest extends AbstractUnitTest {

    @Autowired
    private DaySalesService daySalesService;
    @Autowired
    private DaySalesDao daySalesDao;
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
    private Integer quantity1;
    private Integer quantity2;
    private String barcode2; 
    private OrderItemForm orderItemForm;
    private OrderItemForm orderItemForm2;
    private List<OrderItemForm> orderItemFormList;
    private List<OrderItemPojo> orderItemPojoList;
    private OrderPojo order;
    private Double sellingPrice1;
    private Double sellingPrice2;
    @Before
    public void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.0;
        quantity1 = 10;
        quantity2 = 20;
        sellingPrice1 = 1.11;
        sellingPrice2 = 2.22;

        testUtil.createInventoryCascade(barcode, brand, category, name, mrp, quantity1);
        barcode2 = "abcdef13";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createInventory(barcode2, quantity1);

        orderItemForm = new OrderItemForm(barcode, quantity1, sellingPrice1);
        orderItemForm2 = new OrderItemForm(barcode2, quantity2, sellingPrice2);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);
        orderItemPojoList = new ArrayList<OrderItemPojo>();
        
        order = new OrderPojo();
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
        DaySalesPojo daySales = daySalesService.add(orderItemPojoList);
        daySales = daySalesDao.select(DaySalesPojo.class, daySales.getDate());
        Double expectedRevenue = sellingPrice1 * quantity1 + sellingPrice2 * quantity2;
        assertEquals(orderItemPojoList.size(), daySales.getInvoicedItemsCount());
        assertEquals(1, daySales.getInvoicedOrdersCount());
        assertEquals(expectedRevenue, daySales.getTotalRevenue());
    }
    
    @Test
    public void testUpdate() throws ApiException {
        DaySalesPojo daySales = daySalesService.add(orderItemPojoList);
        daySales = daySalesDao.select(DaySalesPojo.class, daySales.getDate());
        
        daySales = daySalesService.update(orderItemPojoList);
        daySales = daySalesDao.select(DaySalesPojo.class, daySales.getDate());
        Double expectedRevenue = (sellingPrice1 * quantity1 + sellingPrice2 * quantity2) * 2;
        assertEquals(orderItemPojoList.size() * 2, daySales.getInvoicedItemsCount());
        assertEquals(2, daySales.getInvoicedOrdersCount());
        assertEquals(expectedRevenue, daySales.getTotalRevenue());
    }

    @Test
    public void testGetCheck() throws ApiException {
        DaySalesPojo daySales = daySalesService.add(orderItemPojoList);
        daySales = daySalesDao.select(DaySalesPojo.class, daySales.getDate());
        DaySalesPojo daySalesGet = daySalesService.getCheck(daySales.getDate());
        assertEquals(daySales, daySalesGet);
    }


    @Test
    public void testGetCheckDoesNotExist() throws ApiException {
        ZonedDateTime date = ZonedDateTime.now();
        String expectedMessage = "DaySales does not exist with date: " + date;
        Exception exception = assertThrows(ApiException.class, () -> {
            daySalesService.getCheck(date);
        });
        testUtil.validateExceptionMessage(exception, expectedMessage);
    }





}