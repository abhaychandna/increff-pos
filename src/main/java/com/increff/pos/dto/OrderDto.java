package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemPutForm;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PreProcessingUtil;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;

    public OrderData get(Integer id) throws ApiException {
        OrderPojo p = orderService.get(id);
        return ConvertUtil.convert(p, OrderData.class);
    }

    public PaginatedData<OrderData> getAll(Integer start, Integer length, Integer draw) throws ApiException {
        Integer pageNo = start/length;
        Integer pageSize = length;
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
        for (OrderItemForm form : forms) {
            // TODO : REMOVE THIS ?? since validateOrderItems is being called ahead
            PreProcessingUtil.normalizeAndValidate(form);
            OrderItemPojo item = convert(form);
            items.add(item);
        }
        PreProcessingUtil.validateOrderItems(items);
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

    public PaginatedData<OrderItemData> getAllItems(Integer start, Integer length, Integer draw) throws ApiException {
        Integer pageNo = start/length;
        Integer pageSize = length;
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

}