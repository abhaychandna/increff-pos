package com.increff.pdf.util;

import java.io.File;
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

   public static <T> void generateReportXML(List <T> reportData, String filename, HashMap<String,String> headers) throws ApiException {
      try {
            System.out.println("Report Data" + reportData + " " + reportData.size());
            for (T t: reportData) {
                  System.out.println("Report Data" + t);
                  // type of T
                  System.out.println("Type of T: " + t.getClass().getName());
            }
            Document doc = getDocument();
      
            Element rootElement = doc.createElement("ReportForm");
            doc.appendChild(rootElement);

            addHeaders(headers, doc, rootElement);

            Element itemsList = doc.createElement("ItemsList");
            
            // for each key value pair in linkedHashMap reportData
            reportData.forEach(reportDataItem ->{

                  // reportDataItem is a LINKEDHASHMAP
                  Element items = doc.createElement("Items");

                  HashMap linkedHashMap = (HashMap) reportDataItem;
                  linkedHashMap.forEach((key, value) -> {
                        System.out.println("Key: " + key + " Value: " + value);
                        System.out.println("Key Type: " + key.getClass().getName() + " Value Type: " + value.getClass().getName());
                        
                        Element element = doc.createElement(key.toString());
                        element.appendChild(doc.createTextNode(value.toString()));
                        items.appendChild(element);
                  });
                  itemsList.appendChild(items);
            });
            
            rootElement.appendChild(itemsList);
            transformDocumentToXML(filename, doc);
      }
      catch (Exception e) {
            e.printStackTrace();
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
  
  private static void transformDocumentToXML(String filename, Document doc)
  throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filename));
      transformer.transform(source, result);
  
      // Output to console for testing
      StreamResult consoleResult = new StreamResult(System.out);
      transformer.transform(source, consoleResult);
  }
  
  private static Document getDocument() throws ParserConfigurationException {
      DocumentBuilderFactory dbFactory =
          DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.newDocument();
  
      return doc;
  }
}

