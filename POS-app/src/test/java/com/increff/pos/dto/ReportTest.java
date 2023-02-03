package com.increff.pos.dto;

import static org.junit.Assert.assertNotNull;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;

public class ReportTest extends AbstractUnitTest {

    @Autowired
    private ReportDto reportDto;
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
        testUtil.createInventoryCascade(barcode, brand, category, name, mrp, quantity);
        barcode2 = "abcdef13";
        testUtil.createProduct(barcode2, brand, category, name, mrp);
        testUtil.createInventory(barcode2, quantity);

        orderItemForm = new OrderItemForm(barcode, quantity1, sellingPrice1);
        orderItemForm2 = new OrderItemForm(barcode2, quantity2, sellingPrice2);
        orderItemFormList = List.of(orderItemForm, orderItemForm2);
        testUtil.createOrder(orderItemFormList);
    }

    @Test
    public void testBrandReport() throws ApiException {
        String brandReport = reportDto.brandReport();
        assertNotNull(brandReport);
    }

    @Test
    public void testSalesReportAll() throws ApiException {
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        String salesReport = reportDto.salesReport(salesReportForm);
        assertNotNull(salesReport);
    }
    
    @Test
    public void testSalesReportBrandFilter() throws ApiException {
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        salesReportForm.setBrand(brand);
        String salesReport = reportDto.salesReport(salesReportForm);
        assertNotNull(salesReport);
    }

    @Test
    public void testSalesReportCategoryFilter() throws ApiException {
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        salesReportForm.setCategory(category);
        String salesReport = reportDto.salesReport(salesReportForm);
        assertNotNull(salesReport);
    }

    @Test
    public void testSalesReportBrandCategoryFilter() throws ApiException {
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        salesReportForm.setBrand(brand);
        salesReportForm.setCategory(category);
        String salesReport = reportDto.salesReport(salesReportForm);
        assertNotNull(salesReport);
    }

    @Test
    public void testInventoryReport() throws ApiException {
        String inventoryReport = reportDto.inventoryReport();
        assertNotNull(inventoryReport);
    }


}