package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.increff.pos.model.data.DaySalesData;
import com.increff.pos.model.data.PaginatedData;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TimeUtil;



@Component
public class DaySalesDto {
    
    @Autowired
    private DaySalesService svc;
    @Autowired
    private OrderService orderService;
    @Autowired 
    private OrderItemService orderItemService;

    public PaginatedData<DaySalesData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<DaySalesPojo> daySales = svc.getAll(pageNo, pageSize);
        List<DaySalesData> daySaleList = daySales.stream().map(e->convert(e)).collect(Collectors.toList());
        Integer count = svc.getRecordsCount();
        return new PaginatedData<DaySalesData>(daySaleList, draw, count, count);
    }

    public PaginatedData<DaySalesData> getAll(Integer start, Integer pageSize, Integer draw, String strStartDate, String strEndDate) throws ApiException {
        if (strStartDate.isEmpty() || strEndDate.isEmpty()) {
            return getAll(start, pageSize, draw);
        }
        Integer pageNo = start/pageSize;

        ZonedDateTime startDate = TimeUtil.isoTimeStringToZonedDateTime(strStartDate);
        ZonedDateTime endDate = TimeUtil.isoTimeStringToZonedDateTime(strEndDate);
        if(endDate.isBefore(startDate)) {
            throw new ApiException("Start date cannot be greater than end date");
        }
        List<DaySalesPojo> daySales = svc.getAll(pageNo, pageSize, startDate, endDate);
        List<DaySalesData> daySaleList = new ArrayList<DaySalesData>();
        for (DaySalesPojo daySale : daySales) {
            daySaleList.add(convert(daySale));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<DaySalesData>(daySaleList, draw, count, count);
    }

    @Scheduled(fixedDelayString = "${daySalesScheduler.delay.seconds}000")
    public void calculateSales() throws ApiException {
        ZonedDateTime startDate = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endDate = ZonedDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        DaySalesData daySalesData = calculateSales(startDate, endDate);
        daySalesData.setDate(startDate);

        svc.add(ConvertUtil.convert(daySalesData, DaySalesPojo.class));
    }

    
    private DaySalesData calculateSales(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        List<OrderPojo> orders = orderService.filterByDate(startDate, endDate);
        List<Integer> orderIds = orders.stream().map(OrderPojo::getId).collect(Collectors.toList());

        List<OrderItemPojo> orderItems = orderItemService.getByColumn("orderId", orderIds);
        Double totalRevenue = orderItems.stream().mapToDouble(OrderItemPojo::getCost).sum();

        DaySalesData daySalesData = new DaySalesData();
        daySalesData.setInvoicedItemsCount(orderItems.size());
        daySalesData.setInvoicedOrdersCount(orders.size());
        daySalesData.setTotalRevenue(totalRevenue);
        return daySalesData;
    }    

	private DaySalesData convert(DaySalesPojo p) {
		return ConvertUtil.convert(p, DaySalesData.class);
	}
}
