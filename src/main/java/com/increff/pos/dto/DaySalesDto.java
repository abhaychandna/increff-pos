package com.increff.pos.dto;

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

	private DaySalesData convert(DaySalesPojo p) {
		return ConvertUtil.convert(p, DaySalesData.class);
	}
}
