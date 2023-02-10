package com.increff.pos.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.increff.pos.model.data.XSLTFilename;
import com.increff.pos.model.form.PDFForm;
import com.increff.pos.spring.Properties;

@Component
public class PDFClient {

    @Autowired
    private Properties Properties;

    public <T> String getPDFInBase64(List<T> pdfData, XSLTFilename xsltFilename, HashMap<String, String> XMLheaders) throws ApiException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiUrl = Properties.getPdfAppBaseUrl() + Properties.getPdfAppGenerateReportUrl();
        PDFForm<T> pdfForm = new PDFForm<T>(xsltFilename, XMLheaders, pdfData);
        RestTemplate RestTemplate = new RestTemplate();
        ResponseEntity<String> apiResponse = RestTemplate.postForEntity(apiUrl, pdfForm, String.class);
        String base64 = apiResponse.getBody();

        return base64;
    }

    public void saveBase64ToPDF(String base64, String filepath) throws ApiException {
        try {
            byte[] pdfAsBytes = Base64.getDecoder().decode(base64);
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            
            FileOutputStream os = new FileOutputStream(filepath);
            os.write(pdfAsBytes);
            os.close();
        } catch (Exception e) {
            throw new ApiException("Error in saving pdf file." + e.getMessage());
        }
    }

    public String PDFToBase64(String filename) throws ApiException {
        File file = new File(filename);
        try{
            return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
        } catch (Exception e) {
            throw new ApiException("Error in reading pdf file." + e.getMessage());
        }
}

}
