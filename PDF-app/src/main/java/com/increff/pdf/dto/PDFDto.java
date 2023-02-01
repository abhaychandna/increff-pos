package com.increff.pdf.dto;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.increff.pdf.model.PDFForm;
import com.increff.pdf.service.ApiException;
import com.increff.pdf.util.PDFUtil;
import com.increff.pdf.util.XMLUtil;

@Component
public class PDFDto {
    @Value("${xmlFilepath}")
    private String xmlFilepath;
    
    public <T> String generateReport(PDFForm<T> pdfForm) throws ApiException {
        //List<T> reportForm, String xsltFilename, String outputFilename, HashMap<String, String> headers
        System.out.println("Service.genrateReport called ");
        
        // print pdfForm
        System.out.println("XSLT Filename: " + pdfForm.getXsltFilename());
        System.out.println("Headers: " + pdfForm.getHeaders());
        System.out.println("Report Data: " + pdfForm.getReportData());

        List<T> reportForm = pdfForm.getReportData();
        String xsltFilename = pdfForm.getXsltFilename();
        HashMap<String, String> headers = pdfForm.getHeaders();

        File xsltFile = new File(xsltFilename + ".xsl");
        
        xmlFilepath = xmlFilepath + "/xmlFile.xml";
        new File(xmlFilepath).getParentFile().mkdirs();

        try {
            XMLUtil.generateReportXML(reportForm, xmlFilepath, headers);
            String base64 = PDFUtil.generatePDFBase64(xmlFilepath, xsltFile);
            return base64;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in generating report." + e.getMessage());
            throw new ApiException("Error in generating report." + e.getMessage());
        }
    }
}