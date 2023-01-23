package com.increff.pos.util;

import java.io.File;
import java.lang.reflect.Field;
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

import com.increff.pos.model.InvoiceForm;
import com.increff.pos.model.InvoiceItemForm;
import com.increff.pos.service.ApiException;  

public class XMLUtil {
   public static void generateXML(InvoiceForm invoiceForm, String fileName) {

      try {
         Document doc = getDocument();

        Element rootElement = doc.createElement("InvoiceForm");
        doc.appendChild(rootElement);
        
        Element orderId = doc.createElement("OrderId");
        orderId.appendChild(doc.createTextNode("99"));
        rootElement.appendChild(orderId);

        Element time = doc.createElement("Time");
        time.appendChild(doc.createTextNode("212123"));
        rootElement.appendChild(time);

        Element itemsList = doc.createElement("ItemsList");
        Double totalCost = 0.0;
        List<InvoiceItemForm> invoiceForms = invoiceForm.getItems();
        for(Integer i=0;i<invoiceForms.size();i++){
            InvoiceItemForm invoiceItemForm = invoiceForms.get(i);
            
            Element items = doc.createElement("Items");
            
            Element barcode = doc.createElement("barcode");
            barcode.appendChild(doc.createTextNode(invoiceItemForm.getBarcode()));
            items.appendChild(barcode);

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(invoiceItemForm.getName()));
            items.appendChild(name);

            Element productId = doc.createElement("productId");
            productId.appendChild(doc.createTextNode(invoiceItemForm.getProductId().toString()));
            items.appendChild(productId);

            Element quantity = doc.createElement("quantity");
            quantity.appendChild(doc.createTextNode(invoiceItemForm.getQuantity().toString()));
            items.appendChild(quantity);

            Element sellingPrice = doc.createElement("sellingPrice");
            sellingPrice.appendChild(doc.createTextNode(invoiceItemForm.getSellingPrice().toString()));
            items.appendChild(sellingPrice);

            Element costElement = doc.createElement("cost");
            Double cost = (invoiceItemForm.getQuantity() * invoiceItemForm.getSellingPrice() );
            costElement.appendChild(doc.createTextNode( cost.toString()));
            items.appendChild(costElement);

            totalCost += cost;
            itemsList.appendChild(items);
        }

        rootElement.appendChild(itemsList);

        Double gst18  = totalCost * 0.18;
        Double costAfterGst = totalCost + gst18;

        Element totalCostElement = doc.createElement("totalCost");
        totalCostElement.appendChild(doc.createTextNode(totalCost.toString()));
        rootElement.appendChild(totalCostElement);

        Element gst18Element = doc.createElement("gst18");
        gst18Element.appendChild(doc.createTextNode(gst18.toString()));
        rootElement.appendChild(gst18Element);

        Element costAfterGstElement = doc.createElement("costAfterGst");
        costAfterGstElement.appendChild(doc.createTextNode(costAfterGst.toString()));
        rootElement.appendChild(costAfterGstElement);

         transformDocumentToXML(fileName, doc);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   
   public static <T> void generateReportXML(List <T> reportData, String filename, HashMap<String,String> headers) throws ApiException {
      try {
            System.out.println("Report Data" + reportData + " " + reportData.size());
            Document doc = getDocument();
      
            Element rootElement = doc.createElement("ReportForm");
            doc.appendChild(rootElement);

            addHeaders(headers, doc, rootElement);

            Element itemsList = doc.createElement("ItemsList");
            reportData.forEach(inventoryReportDataItem -> {
      
               Element items = doc.createElement("Items");
      
               for (Field field: inventoryReportDataItem.getClass().getDeclaredFields()) {
                  field.setAccessible(true);
                  try {
                        System.out.println("Field Name: " + field.getName());
      
                        System.out.println("Field Value: " + field.get(inventoryReportDataItem));
      
                        Element element = doc.createElement(field.getName());
      
                        element.appendChild(doc.createTextNode(field.get(inventoryReportDataItem).toString()));
                        items.appendChild(element);
      
                  } catch (IllegalAccessException e) {
                        System.out.println("Error in XMLUtil");
                        e.printStackTrace();
                        // TODO : Unable to throw exception here ?? is it cuz of lambda expression?
                        //throw new Exception("Failed to generate XML");
                  }
      
                  itemsList.appendChild(items);
      
               }
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

