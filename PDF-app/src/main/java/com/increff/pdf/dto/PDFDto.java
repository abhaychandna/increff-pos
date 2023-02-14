package com.increff.pdf.dto;

import java.io.File;
import java.util.*;

import com.increff.pdf.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.increff.pdf.model.form.PDFForm;
import com.increff.pdf.service.ApiException;
import com.increff.pdf.util.PDFUtil;
import com.increff.pdf.util.XMLUtil;

@Component
public class PDFDto {

    @Value("${xslt.path}")
    private String xsltPath;

    public <T> String generateReport(PDFForm<T> pdfForm) throws ApiException {
        ValidateUtil.validate(pdfForm);
        List<T> reportForm = pdfForm.getReportData();
        String xsltFilename = pdfForm.getXsltFilename().toString();
        HashMap<String, String> headers = pdfForm.getHeaders();

        File xsltFile = new File(new File(xsltPath + xsltFilename + ".xsl").getAbsolutePath());

        try {
            String xmlBase64 = XMLUtil.generateReportXMLBase64(reportForm, headers);
            
            String base64 = PDFUtil.generatePDFBase64(xsltFile, xmlBase64);
            return base64;
        }
        catch (Exception e){
            throw new ApiException("Error in generating report." + e.getMessage());
        }
    }
}
