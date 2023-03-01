package com.increff.pos.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.increff.pos.scheduler.DaySalesScheduler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.model.data.DaySalesData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.util.TestUtil;

public class DaySalesTest extends AbstractUnitTest {

    @Autowired
    private DaySalesDto daySalesDto;
    @Autowired
    private DaySalesScheduler daySalesScheduler;
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
        testUtil.createInventory(barcode2, quantity2);

        orderItemForm = new OrderItemForm(barcode, quantity1, sellingPrice1);
        orderItemForm2 = new OrderItemForm(barcode2, quantity2, sellingPrice2);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);        
        
        testUtil.createOrder(orderItemFormList);
    }

    @Test
    public void testGetAll() throws ApiException {
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
        testUtil.checkEquals(daySales1, data.get(0));
        testUtil.checkEquals(daySales2, data.get(1));
    }

    @Test
    public void testGetAllDateFilter() throws ApiException {
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
    public void testGetAllDateFilterEmptyStartDate_thenGetAll() throws ApiException {
        ZonedDateTime date = ZonedDateTime.now();
        Integer invoicedOrdersCount = 1;
        Integer invoicedItemsCount = 2;
        Double totalRevenue = 300.0;
        testUtil.createDaySales(date, invoicedOrdersCount, invoicedItemsCount, totalRevenue);
        ZonedDateTime datePlus1 = date.plusDays(1);
        testUtil.createDaySales(datePlus1, invoicedOrdersCount, invoicedItemsCount, totalRevenue);

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");        
        String endDate = ZonedDateTime.now().minusDays(25).format(dateTimeFormat);

        PaginatedData<DaySalesData> paginatedData = daySalesDto.getAll(0, 10, 1, "", endDate);
        List<DaySalesData> data = paginatedData.getData();
        assertEquals(2, data.size());
    }

    @Test
    public void testGetAllDateFilterEmptyEndDate_thenGetAll() throws ApiException {
        ZonedDateTime date = ZonedDateTime.now();
        Integer invoicedOrdersCount = 1;
        Integer invoicedItemsCount = 2;
        Double totalRevenue = 300.0;
        testUtil.createDaySales(date, invoicedOrdersCount, invoicedItemsCount, totalRevenue);
        ZonedDateTime datePlus1 = date.plusDays(1);
        testUtil.createDaySales(datePlus1, invoicedOrdersCount, invoicedItemsCount, totalRevenue);

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");        
        String startDate = ZonedDateTime.now().plusDays(25).format(dateTimeFormat);

        PaginatedData<DaySalesData> paginatedData = daySalesDto.getAll(0, 10, 1, startDate, "");
        List<DaySalesData> data = paginatedData.getData();
        assertEquals(2, data.size());
    }


    @Test
    public void testCalculateDaySales() throws ApiException {
        daySalesScheduler.calculateSales();
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