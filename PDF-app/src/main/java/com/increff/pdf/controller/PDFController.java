package com.increff.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pdf.dto.InvoiceDto;
import com.increff.pdf.model.PDFForm;
import com.increff.pdf.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(value = "/api/pdf")
public class PDFController {
    
    @Autowired
    private InvoiceDto invoiceService;

    // hello world
    @ApiOperation(value = "Adds multiple brands in bulk")
    @RequestMapping(path="/hello", method = RequestMethod.GET)
    public String hello() {
        return "Hello World";
    }

    @ApiOperation(value = "Adds multiple brands in bulk")
    @RequestMapping(path="/generateReport", method = RequestMethod.POST) 
    public <T> String generateReport(@RequestBody PDFForm<T> pdfForm) throws ApiException {
        System.out.println("In controller");
        System.out.println("Output Filename: " + pdfForm.getOutputFilename());
        System.out.println("XSLT Filename: " + pdfForm.getXsltFilename());
        System.out.println("Headers: " + pdfForm.getHeaders());
        System.out.println("Report Data: " + pdfForm.getReportData());
        return invoiceService.generateReport(pdfForm);
    }
}
