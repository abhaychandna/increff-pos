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
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;

@Component
public class OrderItemDto {

    @Autowired
    OrderItemService svc;

    public List<OrderItemData> add(List<OrderItemForm> forms) throws ApiException {
        List<OrderItemPojo> items = new ArrayList<OrderItemPojo>();
        for (OrderItemForm form : forms) {
            OrderItemPojo item = ConvertUtil.convert(form);
            NormalizeUtil.normalize(item);
            items.add(item);
        }
        ValidateUtil.validateOrderItems(items);
        items = svc.add(items);
        List<OrderItemData> itemsData = new ArrayList<OrderItemData>();
        for (OrderItemPojo item : items) {

            itemsData.add(ConvertUtil.convert(item));
        }
        return itemsData;
    }

    public OrderItemData get(Integer id) throws ApiException {
        OrderItemPojo p = svc.get(id);
        return ConvertUtil.convert(p);
    }

    public List<OrderItemData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<OrderItemPojo> items = svc.getAll(pageNo, pageSize);
        List<OrderItemData> respList = new ArrayList<OrderItemData>();
        for (OrderItemPojo p : items) {
            respList.add(ConvertUtil.convert(p)); 
        }
        return respList;
    }

    public void update(Integer id, OrderItemPutForm f) throws ApiException {
        OrderItemPojo p = ConvertUtil.convert(f);
        NormalizeUtil.normalize(p);
        ValidateUtil.validateOrderItemPut(p);
        svc.update(id, p);
    }
}