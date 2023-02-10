package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;

import com.increff.pos.util.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.util.TestUtil;

public class DaySalesServiceTest extends AbstractUnitTest {

    @Autowired
    private DaySalesService daySalesService;
    @Autowired
    private DaySalesDao daySalesDao;
    
    @Autowired
    private TestUtil testUtil;

    private ZonedDateTime today;
    private Integer invoicedOrdersCount;
    private Integer invoicedItemsCount;
    private Double totalRevenue;
    
    @Before
    public void init() throws ApiException {
        today = ZonedDateTime.now();
        invoicedOrdersCount = 2;
        invoicedItemsCount = 5;
        totalRevenue = 1234.56;
    }

    @Test
    public void testUpdate() throws ApiException {
        DaySalesPojo daySales = daySalesService.update(new DaySalesPojo(today, invoicedOrdersCount, invoicedItemsCount, totalRevenue));
        daySales = daySalesDao.select(daySales.getDate());
        assertEquals(today, daySales.getDate());
        assertEquals(invoicedOrdersCount, daySales.getInvoicedOrdersCount());
        assertEquals(invoicedItemsCount, daySales.getInvoicedItemsCount());
        assertEquals(totalRevenue, daySales.getTotalRevenue());
    }

    @Test
    public void testUpdateNonExistingDate_thenAddNewDate() throws ApiException {
        DaySalesPojo daySales = daySalesService.update(new DaySalesPojo(today, invoicedOrdersCount, invoicedItemsCount, totalRevenue));
        Integer expectedItemsCount = 3, expectedOrdersCount = 2;
        Double expectedRevenue = 1000.25;
        DaySalesPojo newDaySales = new DaySalesPojo(daySales.getDate(), expectedOrdersCount, expectedItemsCount, expectedRevenue);
        daySalesService.update(newDaySales);
        daySales = daySalesDao.select(newDaySales.getDate());
        assertEquals(newDaySales.getDate(), daySales.getDate());
        assertEquals(expectedItemsCount, daySales.getInvoicedItemsCount());
        assertEquals(expectedOrdersCount, daySales.getInvoicedOrdersCount());
        assertEquals(expectedRevenue, daySales.getTotalRevenue());
    }

    @Test
    public void testGetCheck() throws ApiException {
        DaySalesPojo daySales = daySalesService.update(new DaySalesPojo(today, invoicedOrdersCount, invoicedItemsCount, totalRevenue));
        daySales = daySalesDao.select(daySales.getDate());
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