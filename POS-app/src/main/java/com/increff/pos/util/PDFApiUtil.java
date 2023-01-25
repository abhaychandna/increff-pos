package com.increff.pos.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.increff.pos.model.PDFForm;
import com.increff.pos.service.ApiException;

public class PDFApiUtil {

    public static <T> String getReportPDFBase64(List<T> reportData, String xsltFilename, String outputFilename, HashMap<String, String> XMLheaders) throws ApiException{
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
        String base64 = apiResponse.getBody();

        // remove ':' from outputFilename
        outputFilename = outputFilename.replace(":", "-");
        System.out.println("Output filename: " + outputFilename);
        String pdfFile = new File(outputFilename + ".pdf").getAbsolutePath();
        // save base64 to pdf file
        saveBase64ToPDF(base64, pdfFile);

        return base64;
    }

    public static void saveBase64ToPDF(String base64, String pdfFile) throws ApiException {
        try {
            byte[] pdfAsBytes = Base64.getDecoder().decode(base64);
            FileOutputStream os = new FileOutputStream(pdfFile);
            os.write(pdfAsBytes);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("Error in saving pdf file." + e.getMessage());
        }
    }

    public static String PDFToBase64(String filename) throws ApiException {
        File file = new File(filename);
        try{
            return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("Error in reading pdf file." + e.getMessage());
        }
}

}
