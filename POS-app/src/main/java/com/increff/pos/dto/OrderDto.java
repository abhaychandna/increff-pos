package com.increff.pos.dto;

import java.io.File;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.data.InvoiceItemData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.data.XSLTFilename;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.Properties;
import com.increff.pos.util.ClientWrapper;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PreProcessingUtil;

import javax.transaction.Transactional;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ClientWrapper ClientWrapper;

    @Autowired
    private Properties Properties;



    public String getInvoice(Integer id) throws ApiException {
        OrderPojo order = orderService.getCheck(id);
        
        List<OrderItemPojo> items = orderItemService.getByOrderId(id);

        String folderPath = Properties.getResourcePath() + "/invoices";
        File folder = new File(folderPath);
        folder.getParentFile().mkdirs();
        
        String outputFilepath = folderPath + "/invoice_" + id + ".pdf";
        String base64 = getCachedInvoice(outputFilepath);
        if(Objects.nonNull(base64)) {
            return base64;
        }

        return generateInvoice(order, items, outputFilepath);
    }

    private String generateInvoice(OrderPojo order, List<OrderItemPojo> items, String outputFilepath) throws ApiException {
        String base64;
        Double total = 0.0;
        List<InvoiceItemData> invoiceItems = new ArrayList<InvoiceItemData>();
        for(OrderItemPojo item: items) {
            ProductPojo product = productService.getCheck(item.getProductId());
            invoiceItems.add(new InvoiceItemData(product.getName(), product.getBarcode(), item.getQuantity(), item.getSellingPrice(), product.getId()));
            total += item.getQuantity() * item.getSellingPrice();
        }

        HashMap<String, String> XMLheaders = new HashMap<String, String>();
        XMLheaders.put("OrderId", order.getId().toString());
        XMLheaders.put("Time", order.getTime().format(new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy HH:mm:ss z").toFormatter()));
        XMLheaders.put("Total", String.format("%.2f", total));

        XSLTFilename xsltFilename = XSLTFilename.INVOICE;
        base64 = ClientWrapper.getPdfClient().getPDFInBase64(invoiceItems, xsltFilename, XMLheaders);

        ClientWrapper.getPdfClient().saveBase64ToPDF(base64, outputFilepath);

        return base64;
    }

    public OrderData get(Integer id) throws ApiException {
        OrderPojo p = orderService.getCheck(id);
        return ConvertUtil.convert(p, OrderData.class);
    }

    public PaginatedData<OrderData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<OrderPojo> orders = orderService.getAll(pageNo, pageSize);
        List<OrderData> orderDataList = new ArrayList<OrderData>();
        for (OrderPojo p : orders) {
            orderDataList.add(ConvertUtil.convert(p, OrderData.class));
        }
        Integer count = orderService.getRecordsCount();
        return new PaginatedData<OrderData>(orderDataList, draw, count, count);
    }

    @Transactional
    public List<OrderItemData> add(List<OrderItemForm> forms) throws ApiException {
        List<OrderItemPojo> items = new ArrayList<OrderItemPojo>();
        PreProcessingUtil.normalizeAndValidate(forms);
        checkDuplicateBarcodes(forms);
        validateOrderQuantityInInventory(forms);

        for (OrderItemForm form : forms) {
            items.add(convert(form));
        }
        orderService.add(items);
        reduceInventoryQuantity(items);
        List<OrderItemData> itemsData = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemsData.add(convert(item));
        }
        return itemsData;
    }

    public List<OrderItemData> getItemsByOrderId(Integer orderId) throws ApiException {
        List<Integer> orderIdList = new ArrayList<Integer>();
        orderIdList.add(orderId);
        List<OrderItemPojo> items = orderItemService.getByColumn("orderId", new ArrayList<>(orderIdList));
        List<OrderItemData> itemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemDataList.add(convert(item));
        }
        return itemDataList;
    }

    public PaginatedData<OrderItemData> getAllItems(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<OrderItemPojo> items = orderItemService.getAll(pageNo, pageSize);
        List<OrderItemData> itemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemDataList.add(convert(item));
        }
        Integer count = orderItemService.getRecordsCount();
        return new PaginatedData<OrderItemData>(itemDataList, draw, count, count);
    }

    private String getCachedInvoice(String outputFilepath) throws ApiException {
        File file = new File(outputFilepath);
        if(file.exists()) {
            return ClientWrapper.getPdfClient().PDFToBase64(outputFilepath);
        }
        return null;        
    }

    private void validateOrderQuantityInInventory(List<OrderItemForm> forms) throws ApiException {
        List<String> errorMessages = new ArrayList<String>();
        for(OrderItemForm form : forms){
            try{
                InventoryPojo inventory = inventoryService.getCheck(productService.getCheckBarcode(form.getBarcode()).getId());
                if(inventory.getQuantity() < form.getQuantity()){
                    errorMessages.add("Insufficient inventory for barcode: " + form.getBarcode() + ". Available: " + inventory.getQuantity() + ", Required: " + form.getQuantity());
                }
            }
            catch (ApiException e){
                errorMessages.add(e.getMessage());
            }
        }
        if(!errorMessages.isEmpty()){
            throw new ApiException(String.join("\n", errorMessages));
        } 
    }

    private OrderItemPojo convert(OrderItemForm form) throws ApiException {
        OrderItemPojo item = ConvertUtil.convert(form, OrderItemPojo.class);
		item.setProductId(productService.getByBarcode(form.getBarcode()).getId());
        return item;
    }

    private OrderItemData convert(OrderItemPojo item) throws ApiException {
        OrderItemData itemData = ConvertUtil.convert(item, OrderItemData.class);
        itemData.setBarcode(productService.getCheck(item.getProductId()).getBarcode());
        return itemData;
    }

    private void checkDuplicateBarcodes(List<OrderItemForm> items) throws ApiException {
        Set<String> barcodes = new HashSet<String>();
        for(OrderItemForm item : items) {
            if(barcodes.contains(item.getBarcode()))
                throw new ApiException("Duplicate barcode: " + item.getBarcode() + " in order");
            barcodes.add(item.getBarcode());
        }
    }

    private void reduceInventoryQuantity(List<OrderItemPojo> items) throws ApiException {
        List<String> errorMessages = new ArrayList<String>();
        for(OrderItemPojo item : items){
            try{
                inventoryService.reduceInventory(item.getProductId(), item.getQuantity());
            }
            catch (ApiException e){
                errorMessages.add(e.getMessage());
            }
        }
        if(!errorMessages.isEmpty()){
            throw new ApiException(String.join("\n", errorMessages));
        }
    }
}