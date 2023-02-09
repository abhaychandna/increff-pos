package com.increff.pdf.dto;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.increff.pdf.model.PDFForm;
import com.increff.pdf.service.ApiException;
import com.increff.pdf.util.PDFUtil;
import com.increff.pdf.util.PreProcessingUtil;
import com.increff.pdf.util.XMLUtil;

@Component
public class PDFDto {
    @Value("${xmlDirectory}")
    private String xmlDirectory;
    
    public <T> String generateReport(PDFForm<T> pdfForm) throws ApiException {
        PreProcessingUtil.validate(pdfForm);
        List<T> reportForm = pdfForm.getReportData();
        String xsltFilename = pdfForm.getXsltFilename().toString();
        HashMap<String, String> headers = pdfForm.getHeaders();

        File xsltFile = new File(xsltFilename + ".xsl");
        
        String xmlFilepath = xmlDirectory + "/xmlFile.xml";
        File xmlFile = new File(xmlFilepath);
        xmlFile.getParentFile().mkdirs();   
        try {
            XMLUtil.generateReportXML(reportForm, xmlFilepath, headers);
            String base64 = PDFUtil.generatePDFBase64(xmlFilepath, xsltFile);
            return base64;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ApiException("Error in generating report." + e.getMessage());
        }
    }
}