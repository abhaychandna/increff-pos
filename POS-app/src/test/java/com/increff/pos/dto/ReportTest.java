package com.increff.pos.dto;

import static org.junit.Assert.assertNotNull;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.BeforeClass;
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
        TestUtil.createOrder(orderItemFormList);
    }

    @Test
    public void testBrandReport() throws ApiException {
        setup();
        String brandReport = reportDto.brandReport();
        assertNotNull(brandReport);
    }

    @Test
    public void testSalesReportAll() throws ApiException {
        setup();
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        String salesReport = reportDto.salesReport(salesReportForm);
        System.out.println(salesReport);
        assertNotNull(salesReport);
    }
    
    @Test
    public void testSalesReportBrandFilter() throws ApiException {
        setup();
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        salesReportForm.setBrand(brand);
        String salesReport = reportDto.salesReport(salesReportForm);
        System.out.println(salesReport);
        assertNotNull(salesReport);
    }

    @Test
    public void testSalesReportCategoryFilter() throws ApiException {
        setup();
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        salesReportForm.setCategory(category);
        String salesReport = reportDto.salesReport(salesReportForm);
        System.out.println(salesReport);
        assertNotNull(salesReport);
    }

    @Test
    public void testSalesReportBrandCategoryFilter() throws ApiException {
        setup();
        String start = ZonedDateTime.now().minusDays(1).toString();
        String end = ZonedDateTime.now().toString();
        SalesReportForm salesReportForm = new SalesReportForm(start, end);
        salesReportForm.setBrand(brand);
        salesReportForm.setCategory(category);
        String salesReport = reportDto.salesReport(salesReportForm);
        System.out.println(salesReport);
        assertNotNull(salesReport);
    }

    @Test
    public void testInventoryReport() throws ApiException {
        setup();
        String inventoryReport = reportDto.inventoryReport();
        assertNotNull(inventoryReport);
    }


}