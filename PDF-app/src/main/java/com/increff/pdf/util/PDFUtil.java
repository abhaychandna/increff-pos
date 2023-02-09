package com.increff.pdf.util;

import java.io.ByteArrayInputStream;
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
    public static String generatePDFBase64(File xsltFile, String xmlBase64)
    throws ApiException {
        try {
            StreamSource xmlSource = getStreamSourceFromXMLBase64(xmlBase64);

            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            OutputStream out;

            out = new java.io.ByteArrayOutputStream();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(xmlSource, res);
            byte[] pdfBytes = ((java.io.ByteArrayOutputStream)out).toByteArray();
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);
            return pdfBase64;
        
        } catch (Exception e) {
            throw new ApiException(e.toString());
        }
    }

    public static String getBase64(String IN_FILE) throws ApiException{
        try {
            byte[] inFileBytes = Files.readAllBytes(Paths.get(IN_FILE));
            byte[] encoded = Base64.getEncoder().encode(inFileBytes);
            String encodedString = new String(encoded);
            return encodedString;
        } catch (Exception e) {
            throw new ApiException("Failed to convert PDF To Base 64. " + e.getMessage());
        }
    }

    private static StreamSource getStreamSourceFromXMLBase64(String base64EncodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
        ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
        return new StreamSource(bais);
    }

}
