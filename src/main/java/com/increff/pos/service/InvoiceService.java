package com.increff.pos.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.increff.pos.model.InvoiceForm;
import com.increff.pos.util.XMLUtil;

public class InvoiceService {

    public void generateInvoice(InvoiceForm invoiceForm) throws FOPException, IOException, TransformerException {
        File xsltFile = new File("invoice.xsl");
        String filename = "invoice_" + invoiceForm.getOrderId();
        String xmlFile = new File(filename + ".xml").getAbsolutePath();
        String pdfFile = new File(filename + ".pdf").getAbsolutePath();

        XMLUtil.generateXML(invoiceForm, xmlFile);

        convertToPDF(xmlFile, xsltFile, pdfFile);
    }

    public void convertToPDF(String xmlFile, File xsltFile, String pdfFile)
    throws IOException, FOPException, TransformerException {

        StreamSource xmlSource = new StreamSource(new File(xmlFile));

        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        OutputStream out;

        out = new java.io.FileOutputStream(pdfFile);

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(xmlSource, res);
        } finally {
            out.close();
        }
    }
}