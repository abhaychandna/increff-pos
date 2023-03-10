package com.increff.pos.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.increff.pos.model.data.*;
import com.increff.pos.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.spring.Properties;
import com.increff.pos.service.ClientWrapper;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PreProcessingUtil;

import javax.transaction.Transactional;

import static com.increff.pos.util.ErrorUtil.throwErrors;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ClientWrapper clientWrapper;

    @Autowired
    private Properties properties;



    public String getInvoice(Integer id) throws ApiException {
        OrderPojo order = orderService.getCheck(id);
        
        List<OrderItemPojo> items = orderService.getItemByOrderId(id);

        String folderPath = properties.getResourcePath() + "/invoices";
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
        double total = 0.0;
        List<InvoiceItemData> invoiceItems = new ArrayList<InvoiceItemData>();
        for(OrderItemPojo item: items) {
            ProductPojo product = productService.getCheck(item.getProductId());
            invoiceItems.add(new InvoiceItemData(product.getName(), product.getBarcode(), item.getQuantity(), item.getSellingPrice(), product.getId()));
            total += item.getQuantity() * item.getSellingPrice();
        }

        HashMap<String, String> XMLheaders = new HashMap<String, String>();
        XMLheaders.put("OrderId", order.getId().toString());
        XMLheaders.put("Time", TimeUtil.getFormattedTime(order.getTime(), "dd-MM-yyyy HH:mm:ss z"));
        XMLheaders.put("Total", String.format("%.2f", total));

        XSLTFilename xsltFilename = XSLTFilename.INVOICE;
        base64 = clientWrapper.getPdfClient().getPDFInBase64(invoiceItems, xsltFilename, XMLheaders);

        clientWrapper.getPdfClient().saveBase64ToPDF(base64, outputFilepath);

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
        for (OrderPojo order : orders) {
            orderDataList.add(ConvertUtil.convert(order, OrderData.class));
        }
        Integer count = orderService.getRecordsCount();
        return new PaginatedData<OrderData>(orderDataList, draw, count, count);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> add(List<OrderItemForm> forms) throws ApiException {
        if(forms.isEmpty()) throwErrors(List.of(new ErrorData<>(null, "No items to add")));
        PreProcessingUtil.normalizeAndValidate(forms);
        checkDuplicateBarcodes(forms);
        validateOrderQuantityInInventory(forms);

        List<OrderItemPojo> items = new ArrayList<OrderItemPojo>();
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
        List<OrderItemPojo> items = orderService.getItemByColumn("orderId", new ArrayList<>(orderIdList));
        List<OrderItemData> itemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemDataList.add(convert(item));
        }
        return itemDataList;
    }

    public PaginatedData<OrderItemData> getAllItems(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<OrderItemPojo> items = orderService.getAllItems(pageNo, pageSize);
        List<OrderItemData> itemDataList = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemDataList.add(convert(item));
        }
        Integer count = orderService.getItemsRecordsCount();
        return new PaginatedData<OrderItemData>(itemDataList, draw, count, count);
    }

    private String getCachedInvoice(String outputFilepath) throws ApiException {
        File file = new File(outputFilepath);
        if(file.exists()) {
            return clientWrapper.getPdfClient().PDFToBase64(outputFilepath);
        }
        return null;        
    }

    private void validateOrderQuantityInInventory(List<OrderItemForm> forms) throws ApiException {
        List<ErrorData<OrderItemForm>> errorList = new ArrayList<ErrorData<OrderItemForm>>();
        for(OrderItemForm form : forms){
            try{
                InventoryPojo inventory = inventoryService.getCheck(productService.getCheckBarcode(form.getBarcode()).getId());
                if(inventory.getQuantity() < form.getQuantity()){
                    throw new ApiException("Insufficient inventory for barcode: " + form.getBarcode() + ". Available: " + inventory.getQuantity() + ", Required: " + form.getQuantity());
                }
            }
            catch (ApiException e){
                errorList.add(new ErrorData<OrderItemForm>(form, e.getMessage()));
            }
        }
        if (!errorList.isEmpty()) {
            throwErrors(errorList);
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
        List<ErrorData<OrderItemForm>> errorList = new ArrayList<ErrorData<OrderItemForm>>();
        for(OrderItemForm item : items) {
            if(barcodes.contains(item.getBarcode()))
                errorList.add(new ErrorData<OrderItemForm>(item, "Duplicate barcode: " + item.getBarcode() + " in order"));
            barcodes.add(item.getBarcode());
        }
        if (!errorList.isEmpty()) throwErrors(errorList);
    }

    private void reduceInventoryQuantity(List<OrderItemPojo> items) throws ApiException {
        List<ErrorData<OrderItemForm>> errorList = new ArrayList<ErrorData<OrderItemForm>>();
        for(OrderItemPojo item : items){
            try{
                inventoryService.reduceInventory(item.getProductId(), item.getQuantity());
            }
            catch (ApiException e){
                errorList.add(new ErrorData<OrderItemForm>(null, e.getMessage()));
            }
        }
        if(!errorList.isEmpty()) throwErrors(errorList);
    }
}