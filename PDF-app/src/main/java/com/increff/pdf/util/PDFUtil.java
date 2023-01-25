package com.increff.pdf.util;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.increff.pdf.service.ApiException;

public class PDFUtil {
    public static String generatePDFBase64(String xmlFile, File xsltFile)
    throws ApiException {
        try {
            StreamSource xmlSource = new StreamSource(new File(xmlFile));

            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            OutputStream out;

            out = new java.io.ByteArrayOutputStream();
            try {
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

                Result res = new SAXResult(fop.getDefaultHandler());

                transformer.transform(xmlSource, res);
            } finally {
                //out.close();
                byte[] pdfBytes = ((java.io.ByteArrayOutputStream)out).toByteArray();
                String base64 = Base64.getEncoder().encodeToString(pdfBytes);
                return base64;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ApiException(e.toString());
        }
    }

    public static String getBase64(String IN_FILE) throws ApiException{
        try {
            byte[] inFileBytes = Files.readAllBytes(Paths.get(IN_FILE));
            byte[] encoded = Base64.getEncoder().encode(inFileBytes);
            String encodedString = new String(encoded);
            System.out.println("ENCODED: " + encodedString);
            return encodedString;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("Failed to convert PDF To Base 64. " + e.getMessage());
        }
    }

}
