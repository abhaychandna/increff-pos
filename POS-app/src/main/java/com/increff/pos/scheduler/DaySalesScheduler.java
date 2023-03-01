package com.increff.pos.scheduler;

import com.increff.pos.dto.DaySalesDto;
import com.increff.pos.model.data.DaySalesData;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.util.ApiException;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class DaySalesScheduler {

    @Autowired
    private DaySalesDto daySalesDto;
    @Autowired
    private DaySalesService svc;
    @Scheduled(fixedDelayString = "${daySalesScheduler.delay.seconds}000")
    public void calculateSales() throws ApiException {
        ZonedDateTime startDate = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endDate = ZonedDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        DaySalesData daySalesData = daySalesDto.calculateSales(startDate, endDate);
        daySalesData.setDate(startDate);

        svc.update(ConvertUtil.convert(daySalesData, DaySalesPojo.class));
    }
}
