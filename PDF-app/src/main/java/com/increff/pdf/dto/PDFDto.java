package com.increff.pdf.dto;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.increff.pdf.model.PDFForm;
import com.increff.pdf.service.ApiException;
import com.increff.pdf.util.PDFUtil;
import com.increff.pdf.util.XMLUtil;

@Component
public class PDFDto {    
    public <T> String generateReport(PDFForm<T> pdfForm) throws ApiException {
        List<T> reportForm = pdfForm.getReportData();
        String xsltFilename = pdfForm.getXsltFilename().toString();
        HashMap<String, String> headers = pdfForm.getHeaders();

        File xsltFile = new File(xsltFilename + ".xsl");
           
        try {
            String xmlBase64 = XMLUtil.generateReportXMLBase64(reportForm, headers);
            
            String base64 = PDFUtil.generatePDFBase64(xsltFile, xmlBase64);
            return base64;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ApiException("Error in generating report." + e.getMessage());
        }
    }
}

