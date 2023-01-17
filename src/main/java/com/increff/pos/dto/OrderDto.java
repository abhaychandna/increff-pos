package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.util.ConvertUtil;

@Component
public class OrderDto {

    @Autowired
    private OrderService svc;

    public OrderData get(Integer id) throws ApiException {
        OrderPojo p = svc.get(id);
        return ConvertUtil.convert(p, OrderData.class);
    }

    public PaginatedData<OrderData> getAll(Integer start, Integer length, Integer draw) throws ApiException {
        Integer pageNo = start/length;
        Integer pageSize = length;
        List<OrderPojo> orders = svc.getAll(pageNo, pageSize);
        List<OrderData> orderDatas = new ArrayList<OrderData>();
        for (OrderPojo p : orders) {
            orderDatas.add(ConvertUtil.convert(p, OrderData.class));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<OrderData>(orderDatas, draw, count, count);
    }

}