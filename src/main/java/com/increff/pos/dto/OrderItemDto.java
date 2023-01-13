package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemPutForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;

@Component
public class OrderItemDto {

    @Autowired
    private OrderItemService svc;
    @Autowired
    private ProductService productService;

    public List<OrderItemData> add(List<OrderItemForm> forms) throws ApiException {
        List<OrderItemPojo> items = new ArrayList<OrderItemPojo>();
        for (OrderItemForm form : forms) {
            OrderItemPojo item = convert(form);
            NormalizeUtil.normalize(item);
            items.add(item);
        }
        ValidateUtil.validateOrderItems(items);
        items = svc.add(items);
        List<OrderItemData> itemsData = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {

            itemsData.add(convert(item));
        }
        return itemsData;
    }

    public OrderItemData get(Integer id) throws ApiException {
        OrderItemPojo p = svc.get(id);
        return convert(p);
    }

    public List<OrderItemData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<OrderItemPojo> items = svc.getAll(pageNo, pageSize);
        List<OrderItemData> respList = new ArrayList<OrderItemData>();
        for (OrderItemPojo p : items) {
            respList.add(convert(p)); 
        }
        return respList;
    }

    public void update(Integer id, OrderItemPutForm form) throws ApiException {
        OrderItemPojo p = ConvertUtil.convert(form, OrderItemPojo.class);
        NormalizeUtil.normalize(p);
        ValidateUtil.validateOrderItemPut(p);
        svc.update(id, p);
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