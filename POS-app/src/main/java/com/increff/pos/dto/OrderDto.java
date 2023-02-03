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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.increff.pos.model.data.InvoiceItemData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.model.form.OrderItemForm;
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

    @Value("${resourcePath}")
    private String resourcePath;



    public String getInvoice(Integer id) throws ApiException {
        OrderPojo order = orderService.getCheck(id);
        
        List<OrderItemPojo> items = orderItemService.getByOrderId(id);

        String folderPath = resourcePath + "/invoices";
        File folder = new File(folderPath);
        folder.getParentFile().mkdirs();
        
        String outputFilename = folderPath + "/invoice_" + id;
        String base64 = getCachedInvoice(outputFilename);
        if(Objects.nonNull(base64)) {
            return base64;
        }
        
        Double total = 0.0;
        List<InvoiceItemData> invoiceItems = new ArrayList<InvoiceItemData>();
        for(OrderItemPojo item: items) {
            InvoiceItemData invoiceItem = new InvoiceItemData();
            invoiceItem.setQuantity(item.getQuantity());
            invoiceItem.setSellingPrice(item.getSellingPrice());
            ProductPojo product = productService.getCheck(item.getProductId());
            invoiceItem.setBarcode(product.getBarcode());
            invoiceItem.setProductId(product.getId());
            invoiceItem.setName(product.getName());

            total += item.getQuantity() * item.getSellingPrice();
            invoiceItems.add(invoiceItem);
        }        

        HashMap<String, String> XMLheaders = new HashMap<String, String>();
        XMLheaders.put("OrderId", order.getId().toString());
        XMLheaders.put("Time", order.getTime().format(new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy HH:mm:ss z").toFormatter()));
        XMLheaders.put("Total", String.format("%.2f", total));
        
        String xsltFilename = "invoice";

        base64 = PDFApiUtil.getReportPDFBase64(invoiceItems, xsltFilename, XMLheaders);
        
        PDFApiUtil.saveBase64ToPDF(base64, outputFilename);

        return base64;

    }

    private String getCachedInvoice(String outputFilename) throws ApiException {
        outputFilename = outputFilename + ".pdf";
        File file = new File(outputFilename);
        if(file.exists()) {
            return PDFApiUtil.PDFToBase64(outputFilename);
        }
        return null;        
    }

    public OrderData get(Integer id) throws ApiException {
        OrderPojo p = orderService.getCheck(id);
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
        OrderItemPojo p = orderItemService.getCheck(id);
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

    private void checkDuplicateBarcodesInOrder(List<OrderItemForm> items) throws ApiException {
        Set<String> barcodes = new HashSet<String>();
        for(OrderItemForm item : items) {
            if(barcodes.contains(item.getBarcode()))
                throw new ApiException("Duplicate barcode: " + item.getBarcode() + " in order");
            barcodes.add(item.getBarcode());
        }
    }
}