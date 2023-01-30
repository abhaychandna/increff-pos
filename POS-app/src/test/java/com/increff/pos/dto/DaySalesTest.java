package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class DaySalesTest extends AbstractUnitTest {

    @Autowired
    private DaySalesDto daySalesDto;
    @Autowired
    private OrderDto orderDto;

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
    private static Double sellingPrice;
    private static Integer daySalesCount;
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
        TestUtil.createInventory(barcode, brand, category, name, mrp, quantity);
        barcode2 = "abcdef13";
        TestUtil.createProduct(barcode2, brand, category, name, mrp);
        TestUtil.createInventorySingle(barcode2, quantity);

        orderItemForm = new OrderItemForm(barcode, 1, 1.11);
        orderItemForm2 = new OrderItemForm(barcode2, 2, 2.22);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);
        
        orderDto.add(orderItemFormList);
        
    }

    @Test
    public void testGetAll() throws ApiException {
        setup();
        PaginatedData<DaySalesData> paginatedData = daySalesDto.getAll(0, 10, 1);
        List<DaySalesData> data = paginatedData.getData();
        assertEquals(1, data.size());
    }

    @Test
    public void testGetAllDateFilter() throws ApiException {
        setup();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX") ;        
        String startDate = ZonedDateTime.now().minusDays(1).format(dateTimeFormat);
        String endDate = ZonedDateTime.now().plusDays(1).format(dateTimeFormat);
        PaginatedData<DaySalesData> paginatedData = daySalesDto.getAll(0, 10, 1, startDate, endDate);
        List<DaySalesData> data = paginatedData.getData();
        assertEquals(1, data.size());
    }



}