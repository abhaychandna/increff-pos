package com.increff.pdf.dto;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.increff.pdf.model.PDFForm;
import com.increff.pdf.model.data.XSLTFilename;
import com.increff.pdf.service.ApiException;
import com.increff.pdf.util.PDFUtil;
import com.increff.pdf.util.XMLUtil;

@Component
public class PDFDto {
    @Value("${xmlDirectory}")
    private String xmlDirectory;
    
    public <T> String generateReport(PDFForm<T> pdfForm) throws ApiException {
        validate(pdfForm);
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

    private <T> void validate(PDFForm<T> pdfForm) throws ApiException {
        validateNull(pdfForm);
        validateXSLTEnum(pdfForm.getXsltFilename());
        validateHeaders(pdfForm.getHeaders(), pdfForm.getXsltFilename());
        validateReportData(pdfForm.getReportData(), pdfForm.getXsltFilename());
    }

    private void validateXSLTEnum(String xsltFilename) throws ApiException {
        List<String> enumValues = List.of(XSLTFilename.class.getEnumConstants()).stream().map(String::valueOf).collect(Collectors.toList());
        if (!enumValues.contains(xsltFilename)) throw new ApiException("Invalid XSLT filename: " + xsltFilename + ". Valid XSLT filenames are: " + enumValues);
    }

    private static <T> void validateNull(PDFForm<T> pdfForm) throws ApiException {
        List<String> errors = new ArrayList<String>();
        if (Objects.isNull(pdfForm.getXsltFilename())) {
            errors.add("XSLT filename is null");
        }
        if (Objects.isNull(pdfForm.getReportData())) {
            errors.add("Report data is null");
        }
        if(errors.size() > 0){
            throw new ApiException(String. join(".\n", errors));
        }
    }

    private void validateHeaders(HashMap<String, String> headers, String xsltFilename) throws ApiException {
        Set<String> validHeaders = getValidHeaders(xsltFilename);
        if(validHeaders.size() == 0) return;
        if(!validHeaders.equals(headers.keySet())) throw new ApiException("Invalid headers: " + headers.keySet() + ". Valid headers are: " + validHeaders);
    }

    private <T> void validateReportData(List<T> reportDataList, String xsltFilename) throws ApiException {
        Set<String> validKeys = getValidKeys(xsltFilename);
        for (T reportData : reportDataList) {
            HashMap<String, String> map = (HashMap<String, String>) reportData;
            if(!map.keySet().equals(validKeys)) throw new ApiException("Invalid report data fields: " + map.keySet() + ". Valid fields are: " + validKeys);
        }
    }

    private Set<String> getValidKeys(String xsltFilename) {
        HashMap<String, Set<String>> validKeysMap = new HashMap<String, Set<String>>();
        validKeysMap.put("brandReport", new HashSet<String>(Arrays.asList("brand", "category")));
        validKeysMap.put("inventoryReport", new HashSet<String>(Arrays.asList("brand", "category", "quantity")));
        validKeysMap.put("salesReport", new HashSet<String>(Arrays.asList("brand", "category", "quantity", "revenue")));
        validKeysMap.put("invoice", new HashSet<String>(Arrays.asList("name", "barcode", "quantity", "sellingPrice", "productId")));
        return validKeysMap.get(xsltFilename);
    }

    private Set<String> getValidHeaders(String xsltFilename) {
        HashMap<String, Set<String>> validKeysMap = new HashMap<String, Set<String>>();
        validKeysMap.put("brandReport", new HashSet<>());
        validKeysMap.put("inventoryReport", new HashSet<String>());
        validKeysMap.put("salesReport", new HashSet<String>(Arrays.asList("startDate", "endDate", "brand", "category")));
        validKeysMap.put("invoice", new HashSet<String>(Arrays.asList("OrderId", "Time", "Total")));
        return validKeysMap.get(xsltFilename);
    }
}