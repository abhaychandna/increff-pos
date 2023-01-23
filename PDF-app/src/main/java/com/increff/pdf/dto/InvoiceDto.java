package com.increff.pdf.dto;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.increff.pdf.model.PDFForm;
import com.increff.pdf.service.ApiException;
import com.increff.pdf.util.PDFUtil;
import com.increff.pdf.util.XMLUtil;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InvoiceDto {

    // public void generateInvoice(InvoiceForm invoiceForm) throws FOPException, IOException, TransformerException, ApiException {
    //     File xsltFile = new File("invoice.xsl");
    //     String filename = "invoice_" + invoiceForm.getOrderId();
    //     String xmlFile = new File(filename + ".xml").getAbsolutePath();
    //     String pdfFile = new File(filename + ".pdf").getAbsolutePath();

    //     XMLUtil.generateXML(invoiceForm, xmlFile);

    //     PDFUtil.generatePDF(xmlFile, xsltFile, pdfFile);
    // }

    public <T> String generateReport(PDFForm<T> pdfForm) throws ApiException {
        //List<T> reportForm, String xsltFilename, String outputFilename, HashMap<String, String> headers
        System.out.println("Service.genrateReport called ");
        
        // print pdfForm
        System.out.println("Output Filename: " + pdfForm.getOutputFilename());
        System.out.println("XSLT Filename: " + pdfForm.getXsltFilename());
        System.out.println("Headers: " + pdfForm.getHeaders());
        System.out.println("Report Data: " + pdfForm.getReportData());

        List<T> reportForm = pdfForm.getReportData();
        String xsltFilename = pdfForm.getXsltFilename();
        String outputFilename = pdfForm.getOutputFilename();
        HashMap<String, String> headers = pdfForm.getHeaders();

        // remove ':' from outputFilename
        outputFilename = outputFilename.replace(":", "-");
        System.out.println("Output filename: " + outputFilename);
        File xsltFile = new File(xsltFilename + ".xsl");
        String xmlFile = new File(outputFilename + ".xml").getAbsolutePath();
        String pdfFile = new File(outputFilename + ".pdf").getAbsolutePath();

        try {
            XMLUtil.generateReportXML(reportForm, xmlFile, headers);
            PDFUtil.generatePDF(xmlFile, xsltFile, pdfFile);
            String base64 = PDFUtil.getBase64(pdfFile);
            return base64;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in generating report." + e.getMessage());
            throw new ApiException("Error in generating report." + e.getMessage());
        }
    }

    /*
    public String getBase64(String IN_FILE) throws ApiException {
        try {
            XMLUtil.generateReportXML(reportForm, xmlFile, headers);
            PDFUtil.generatePDF(xmlFile, xsltFile, pdfFile);
            String base64 = PDFUtil.getBase64(pdfFile);
            return base64;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in generating report." + e.getMessage());
            throw new ApiException("Error in generating report." + e.getMessage());
        }
        return PDFUtil.getBase64(IN_FILE);
    }
     */
}