package com.increff.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pdf.dto.PDFDto;
import com.increff.pdf.model.form.PDFForm;
import com.increff.pdf.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(value = "/api/pdf")
public class PDFController {
    
    @Autowired
    private PDFDto invoiceService;

    @ApiOperation(value = "Generates Report PDF in Base64")
    @RequestMapping(path="/generateReport", method = RequestMethod.POST) 
    public <T> String generateReport(@RequestBody PDFForm<T> pdfForm) throws ApiException {
        return invoiceService.generateReport(pdfForm);
    }
}
