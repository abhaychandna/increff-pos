package com.increff.pdf.util;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.increff.pdf.service.ApiException;  

public class XMLUtil {

   public static <T> String generateReportXMLBase64(List <T> reportData, HashMap<String,String> headers) throws ApiException {
      try {
            Document doc = getDocument();
      
            Element rootElement = doc.createElement("ReportForm");
            doc.appendChild(rootElement);

            addHeaders(headers, doc, rootElement);

            Element itemsList = doc.createElement("ItemsList");
            
            reportData.forEach(reportDataItem ->{
                  Element items = doc.createElement("Items");
                  HashMap linkedHashMap = (HashMap) reportDataItem;
                  linkedHashMap.forEach((key, value) -> {
                        Element element = doc.createElement(key.toString());
                        element.appendChild(doc.createTextNode(value.toString()));
                        items.appendChild(element);
                  });
                  itemsList.appendChild(items);
            });
            
            rootElement.appendChild(itemsList);
            return transformDocumentToXMLBase64(doc);
      }
      catch (Exception e) {
            throw new ApiException("Failed to generate XML");
      }
   }


   private static void addHeaders(HashMap<String, String> headers, Document doc, Element rootElement) {
      if(Objects.isNull(headers) || headers.isEmpty())
            return;
      headers.forEach((key, value) -> {
            Element header = doc.createElement(key);
            header.appendChild(doc.createTextNode(value));
            rootElement.appendChild(header);
      });
   }
  
  private static String transformDocumentToXMLBase64(Document doc)
  throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      transformer.transform(new DOMSource(doc), new StreamResult(bos));

      byte[] xmlBytes = bos.toByteArray();
      String encodedXML = Base64.getEncoder().encodeToString(xmlBytes);
      
      return encodedXML;
  }
  
  private static Document getDocument() throws ParserConfigurationException {
      DocumentBuilderFactory dbFactory =
      DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.newDocument();
  
      return doc;
  }
}

