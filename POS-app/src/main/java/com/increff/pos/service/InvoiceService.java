package com.increff.pos.service;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOPException;

import com.increff.pos.model.InvoiceForm;
import com.increff.pos.util.PDFUtil;
import com.increff.pos.util.XMLUtil;

public class InvoiceService {

    public void generateInvoice(InvoiceForm invoiceForm) throws FOPException, IOException, TransformerException, ApiException {
        File xsltFile = new File("invoice.xsl");
        String filename = "invoice_" + invoiceForm.getOrderId();
        String xmlFile = new File(filename + ".xml").getAbsolutePath();
        String pdfFile = new File(filename + ".pdf").getAbsolutePath();

        XMLUtil.generateXML(invoiceForm, xmlFile);

        PDFUtil.generatePDF(xmlFile, xsltFile, pdfFile);
    }
}