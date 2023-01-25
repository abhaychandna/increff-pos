package com.increff.pos.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.InvoiceItemData;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemPutForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PDFApiUtil;
import com.increff.pos.util.PreProcessingUtil;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;



    public String getInvoice(Integer id) throws ApiException {
        OrderPojo order = orderService.get(id);
        
        List<OrderItemPojo> items = orderItemService.getByOrderId(id);
        
        String outputFilename = "invoice_" + id;
        String base64 = invoiceBase64(outputFilename);
        if(Objects.nonNull(base64)) {
            return base64;
        }
        
        Double total = 0.0;
        List<InvoiceItemData> invoiceItems = new ArrayList<InvoiceItemData>();
        for(OrderItemPojo item: items) {
            InvoiceItemData invoiceItem = new InvoiceItemData();
            invoiceItem.setQuantity(item.getQuantity());
            invoiceItem.setSellingPrice(item.getSellingPrice());
            ProductPojo product = productService.get(item.getProductId());
            invoiceItem.setBarcode(product.getBarcode());
            invoiceItem.setProductId(product.getId());
            invoiceItem.setName(product.getName());

            total += item.getQuantity() * item.getSellingPrice();
            invoiceItems.add(invoiceItem);
        }        

        HashMap<String, String> XMLheaders = new HashMap<String, String>();
        XMLheaders.put("OrderId", order.getId().toString());
        XMLheaders.put("Time", order.getTime().toString());
        XMLheaders.put("Total", total.toString());
        String xsltFilename = "invoice";

        return PDFApiUtil.getReportPDFBase64(invoiceItems, xsltFilename, outputFilename, XMLheaders);

    }

    private String invoiceBase64(String outputFilename) throws ApiException {
        outputFilename = outputFilename + ".pdf";
        File file = new File(outputFilename);
        if(file.exists()) {
            return PDFApiUtil.PDFToBase64(outputFilename);
        }
        return null;        
    }

    public OrderData get(Integer id) throws ApiException {
        OrderPojo p = orderService.get(id);
        return ConvertUtil.convert(p, OrderData.class);
    }

    public PaginatedData<OrderData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<OrderPojo> orders = orderService.getAll(pageNo, pageSize);
        List<OrderData> orderDatas = new ArrayList<OrderData>();
        for (OrderPojo p : orders) {
            orderDatas.add(ConvertUtil.convert(p, OrderData.class));
        }
        Integer count = orderService.getRecordsCount();
        return new PaginatedData<OrderData>(orderDatas, draw, count, count);
    }

    public List<OrderItemData> add(List<OrderItemForm> forms) throws ApiException {
        List<OrderItemPojo> items = new ArrayList<OrderItemPojo>();
        PreProcessingUtil.normalizeAndValidate(forms);
        checkDuplicateBarcodesInOrder(forms);
        for (OrderItemForm form : forms) {
            OrderItemPojo item = convert(form);
            items.add(item);
        }
        items = orderService.add(items);
        List<OrderItemData> itemsData = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemsData.add(convert(item));
        }
        return itemsData;
    }

    public OrderItemData getItem(Integer id) throws ApiException {
        OrderItemPojo p = orderItemService.get(id);
        return convert(p);
    }

    
    public List<OrderItemData> getItemsByOrderId(Integer orderId) throws ApiException {
        List<Integer> orderIdList = new ArrayList<Integer>();
        orderIdList.add(orderId);
        List<OrderItemPojo> items = orderItemService.getByColumn("orderId", new ArrayList<>(orderIdList));
        List<OrderItemData> itemDatas = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemDatas.add(convert(item));
        }
        return itemDatas;
    }

    public PaginatedData<OrderItemData> getAllItems(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<OrderItemPojo> items = orderItemService.getAll(pageNo, pageSize);
        List<OrderItemData> itemDatas = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {
            itemDatas.add(convert(item));
        }
        Integer count = orderItemService.getRecordsCount();
        return new PaginatedData<OrderItemData>(itemDatas, draw, count, count);
    }

    public void update(Integer id, OrderItemPutForm form) throws ApiException {
        PreProcessingUtil.normalizeAndValidate(form);
        OrderItemPojo p = ConvertUtil.convert(form, OrderItemPojo.class);
        orderItemService.update(id, p);
    }

    private OrderItemPojo convert(OrderItemForm form) throws ApiException {
        OrderItemPojo item = ConvertUtil.convert(form, OrderItemPojo.class);
		item.setProductId(productService.getByBarcode(form.getBarcode()).getId());
        return item;
    }

    private OrderItemData convert(OrderItemPojo item) throws ApiException {
        OrderItemData itemData = ConvertUtil.convert(item, OrderItemData.class);
        itemData.setBarcode(productService.get(item.getProductId()).getBarcode());
        return itemData;
    }

    private void checkDuplicateBarcodesInOrder(List<OrderItemForm> items) throws ApiException {
        Set<String> barcodes = new HashSet<String>();
        for(OrderItemForm item : items) {
            if(barcodes.contains(item.getBarcode()))
                throw new ApiException("Duplicate barcode: " + item.getBarcode() + " in order");
            barcodes.add(item.getBarcode());
        }
    }
}