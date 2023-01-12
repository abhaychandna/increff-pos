package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.OrderData;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.util.ConvertUtil;

@Component
public class OrderDto {

    @Autowired
    OrderService svc;

    public OrderData get(Integer id) throws ApiException {
        OrderPojo p = svc.get(id);
        return ConvertUtil.convert(p, OrderData.class);
    }

    public List<OrderData> getAll(Integer pageNo, Integer pageSize) throws ApiException {
        List<OrderPojo> items = svc.getAll(pageNo, pageSize);
        List<OrderData> respList = new ArrayList<OrderData>();
        for (OrderPojo p : items) {
            respList.add(ConvertUtil.convert(p, OrderData.class)); 
        }
        return respList;
    }

}