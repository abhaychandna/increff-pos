package com.increff.pdf.util;

import com.increff.pdf.model.form.PDFForm;
import com.increff.pdf.model.data.XSLTFilename;
import com.increff.pdf.service.ApiException;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateUtil {
    public static <T> void validate(PDFForm<T> pdfForm) throws ApiException {
        validateNull(pdfForm);
        normalize(pdfForm);
        validateXSLTEnum(pdfForm.getXsltFilename());
        validateData(pdfForm);
    }

    private static <T> void normalize(PDFForm<T> pdfForm) {
        pdfForm.setXsltFilename(pdfForm.getXsltFilename().toUpperCase());
    }

    private static void validateXSLTEnum(String xsltFilename) throws ApiException {
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

    private static void validateData(PDFForm<?> pdfForm) throws ApiException {
        List<String> errors = new ArrayList<String>();
        errors.addAll(validateHeaders(pdfForm.getHeaders(), pdfForm.getXsltFilename()));
        errors.addAll(validateReportData(pdfForm.getReportData(), pdfForm.getXsltFilename()));
        if(errors.size() > 0){
            throw new ApiException(String. join(".\n", errors));
        }
    }

    private static List<String> validateHeaders(HashMap<String, String> headers, String xsltFilename) throws ApiException {
        Set<String> validHeaders = getValidHeaders(xsltFilename);
        List<String> errors = new ArrayList<String>();
        if(validHeaders.size() !=0 && !validHeaders.equals(headers.keySet())) return List.of("Invalid headers: " + headers.keySet() + ". Valid headers are: " + validHeaders);
        return errors;
    }

    private static <T> List<String> validateReportData(List<T> reportDataList, String xsltFilename) throws ApiException {
        if(reportDataList.size() == 0) return List.of("Report data cannot be empty");

        Set<String> validKeys = getValidKeys(xsltFilename);
        List<String> errors = new ArrayList<String>();
        for (T reportData : reportDataList) {
            HashMap<String, String> map = (HashMap<String, String>) reportData;
            if(!map.keySet().equals(validKeys)) errors.add("Invalid report data fields: " + map.keySet());
        }
        if(errors.size() > 0) errors.add("Valid data fields are: " + validKeys);
        return errors;
    }

    private static Set<String> getValidKeys(String xsltFilename) {
        HashMap<String, Set<String>> validKeysMap = new HashMap<String, Set<String>>();
        validKeysMap.put("BRAND_REPORT", new HashSet<String>(Arrays.asList("brand", "category")));
        validKeysMap.put("INVENTORY_REPORT", new HashSet<String>(Arrays.asList("brand", "category", "quantity")));
        validKeysMap.put("SALES_REPORT", new HashSet<String>(Arrays.asList("brand", "category", "quantity", "revenue")));
        validKeysMap.put("INVOICE", new HashSet<String>(Arrays.asList("name", "barcode", "quantity", "sellingPrice", "productId")));
        return validKeysMap.get(xsltFilename);
    }

    private static Set<String> getValidHeaders(String xsltFilename) {
        HashMap<String, Set<String>> validKeysMap = new HashMap<String, Set<String>>();
        validKeysMap.put("BRAND_REPORT", new HashSet<>());
        validKeysMap.put("INVENTORY_REPORT", new HashSet<String>());
        validKeysMap.put("SALES_REPORT", new HashSet<String>(Arrays.asList("startDate", "endDate", "brand", "category")));
        validKeysMap.put("INVOICE", new HashSet<String>(Arrays.asList("OrderId", "Time", "Total")));
        return validKeysMap.get(xsltFilename);
    }
}
