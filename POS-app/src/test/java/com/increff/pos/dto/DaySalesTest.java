package com.increff.pos.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class DaySalesTest extends AbstractUnitTest {

    @Autowired
    private DaySalesDto daySalesDto;
    @Autowired
    private TestUtil testUtil;

    private static String brand;
    private static String category;
    private static String barcode;
    private static String name;
    private static Double mrp;
    private static Integer quantity1;
    private static Integer quantity2;
    private static String barcode2; 
    private static OrderItemForm orderItemForm;
    private static OrderItemForm orderItemForm2;
    private static List<OrderItemForm> orderItemFormList;
    private static Double sellingPrice1;
    private static Double sellingPrice2;
    @BeforeClass
    public static void init() throws ApiException {
        brand = "adidas";
        category = "tshirts";
        barcode = "abcdef12";
        name = "polo";
        mrp = 100.0;
        quantity1 = 10;
        quantity2 = 20;
        sellingPrice1 = 1.11;
        sellingPrice2 = 2.22;
    }

    private void setup() throws ApiException{
        testUtil.createInventory(barcode, brand, category, name, mrp, quantity1);
        barcode2 = "abcdef13";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createInventorySingle(barcode2, quantity1);

        orderItemForm = new OrderItemForm(barcode, quantity1, sellingPrice1);
        orderItemForm2 = new OrderItemForm(barcode2, quantity2, sellingPrice2);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);        
        
        testUtil.createOrder(orderItemFormList);
    }

    @Test
    public void testGetAll() throws ApiException {
        setup();
        ZonedDateTime date = ZonedDateTime.now();
        Integer invoicedOrdersCount = 1;
        Integer invoicedItemsCount = 2;
        Double totalRevenue = 300.0;
        DaySalesPojo daySales1 = testUtil.createDaySales(date, invoicedOrdersCount, invoicedItemsCount, totalRevenue);
        ZonedDateTime datePlus1 = date.plusDays(1);
        DaySalesPojo daySales2 = testUtil.createDaySales(datePlus1, invoicedOrdersCount, invoicedItemsCount, totalRevenue);

        PaginatedData<DaySalesData> paginatedData = daySalesDto.getAll(0, 10, 1);
        List<DaySalesData> data = paginatedData.getData();
        assertEquals(2, data.size());
        checkEquals(daySales1, data.get(0));
        checkEquals(daySales2, data.get(1));
    }

    private void checkEquals(DaySalesPojo daySales1, DaySalesData daySalesData) {
        assertEquals(daySales1.getDate().withNano(0), daySalesData.getDate().withNano(0));
        assertEquals(daySales1.getInvoicedOrdersCount(), daySalesData.getInvoicedOrdersCount());
        assertEquals(daySales1.getInvoicedItemsCount(), daySalesData.getInvoicedItemsCount());
        assertEquals(daySales1.getTotalRevenue(), daySalesData.getTotalRevenue());
    }

    @Test
    public void testGetAllDateFilter() throws ApiException {
        setup();
        ZonedDateTime date = ZonedDateTime.now();
        Integer invoicedOrdersCount = 1;
        Integer invoicedItemsCount = 2;
        Double totalRevenue = 300.0;
        testUtil.createDaySales(date, invoicedOrdersCount, invoicedItemsCount, totalRevenue);
        ZonedDateTime datePlus1 = date.plusDays(1);
        testUtil.createDaySales(datePlus1, invoicedOrdersCount, invoicedItemsCount, totalRevenue);

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");        
        String startDate = ZonedDateTime.now().minusDays(1).format(dateTimeFormat);
        String startDatePlus2 = ZonedDateTime.now().plusDays(2).format(dateTimeFormat);
        PaginatedData<DaySalesData> paginatedData = daySalesDto.getAll(0, 10, 1, startDate, startDatePlus2);
        List<DaySalesData> data = paginatedData.getData();
        assertEquals(2, data.size());
    }

    @Test
    public void testCalculateDaySales() throws ApiException {
        setup();
        daySalesDto.calculateSales();
        PaginatedData<DaySalesData> paginatedData = daySalesDto.getAll(0, 10, 1);
        List<DaySalesData> data = paginatedData.getData();
        assertEquals(1, data.size());
        DaySalesData daySales = data.get(0);
        assertEquals(1, daySales.getInvoicedOrdersCount());
        assertEquals(orderItemFormList.size(), daySales.getInvoicedItemsCount());
        Double expectedRevenue = orderItemFormList.stream().mapToDouble(e->e.getQuantity() * e.getSellingPrice()).sum();
        assertEquals(expectedRevenue, daySales.getTotalRevenue());
    }



}