package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.PaginatedData;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TimeUtil;



@Component
public class DaySalesDto {
    
    @Autowired
    private DaySalesService svc;

    public PaginatedData<DaySalesData> getAll(Integer start, Integer pageSize, Integer draw) throws ApiException {
        Integer pageNo = start/pageSize;
        List<DaySalesPojo> daySales = svc.getAll(pageNo, pageSize);
        List<DaySalesData> daySaleList = new ArrayList<DaySalesData>();
        for (DaySalesPojo daySale : daySales) {
            daySaleList.add(convert(daySale));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<DaySalesData>(daySaleList, draw, count, count);
    }

    public PaginatedData<DaySalesData> getAll(Integer start, Integer pageSize, Integer draw, String strStartDate, String strEndDate) throws ApiException {
        System.out.println("startDate: " + strStartDate + ", endDate: " + strEndDate);
        System.out.println("start: " + start + ", pageSize: " + pageSize + ", draw: " + draw + ", startDate: " + strStartDate + ", endDate: " + strEndDate);
        if (strStartDate.isEmpty() || strEndDate.isEmpty()) {
            return getAll(start, pageSize, draw);
        }

        Integer pageNo = start/pageSize;

        DateTimeFormatter format = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ZonedDateTime startDate = TimeUtil.getZonedDateTime(strStartDate, format);
        ZonedDateTime endDate = TimeUtil.getZonedDateTime(strEndDate, format);

        List<DaySalesPojo> daySales = svc.getAll(pageNo, pageSize, startDate, endDate);
        List<DaySalesData> daySaleList = new ArrayList<DaySalesData>();
        for (DaySalesPojo daySale : daySales) {
            daySaleList.add(convert(daySale));
        }
        Integer count = svc.getRecordsCount();
        return new PaginatedData<DaySalesData>(daySaleList, draw, count, count);
    }

	private DaySalesData convert(DaySalesPojo p) {
		return ConvertUtil.convert(p, DaySalesData.class);
	}
}
