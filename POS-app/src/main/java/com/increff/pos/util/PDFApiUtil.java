package com.increff.pos.util;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.increff.pos.model.PDFForm;

public class PDFApiUtil {

    public static <T> String getReportPDFBase64(List<T> reportData, String xsltFilename, String outputFilename, HashMap<String, String> XMLheaders){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiUrl = "http://localhost:8000/pdf/api/pdf/generateReport";// + path;
        //PDFForm<T> pdfForm = new PDFForm(reportData, xsltFilename, outputFilename, XMLheaders);
        PDFForm<T> pdfForm = new PDFForm<T>();
        pdfForm.setOutputFilename(outputFilename);
        pdfForm.setXsltFilename(xsltFilename);
        pdfForm.setReportData(reportData);
        pdfForm.setHeaders(XMLheaders);

        System.out.println("PDF form: " + pdfForm.getOutputFilename());
        System.out.println("PDF form: " + pdfForm.getXsltFilename());
        System.out.println("PDF form: " + pdfForm.getReportData());

        RestTemplate RestTemplate = new RestTemplate();
        ResponseEntity<String> apiResponse = RestTemplate.postForEntity(apiUrl, pdfForm, String.class);
        String responseBody = apiResponse.getBody();

        return responseBody;
    }

}
