package com.increff.pos.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class DaySalesService {

	@Autowired
	private DaySalesDao dao;

	public DaySalesPojo add(List<OrderItemPojo> items) throws ApiException {
		DaySalesPojo daySales = new DaySalesPojo();
		ZonedDateTime date = getCurrentZonedDateWithoutTime();
		
		System.out.println("Final date before server add " + date);
		daySales.setDate(date);
		daySales.setInvoicedItemsCount(items.size());
		daySales.setInvoicedOrdersCount(1);
		daySales.setTotalRevenue(totalRevenue(items));
		
		return dao.insert(daySales);
	}

	public DaySalesPojo update(List<OrderItemPojo> items) throws ApiException {
		ZonedDateTime date = getCurrentZonedDateWithoutTime();
		DaySalesPojo daySales = dao.select(DaySalesPojo.class, date);
		if(Objects.isNull(daySales)) {
			return add(items);
		}
		daySales.setInvoicedItemsCount(daySales.getInvoicedItemsCount() + items.size());
		daySales.setInvoicedOrdersCount(daySales.getInvoicedOrdersCount() + 1);
		daySales.setTotalRevenue(daySales.getTotalRevenue() + totalRevenue(items));
		return daySales;
	}

	public Double totalRevenue(List<OrderItemPojo> items) throws ApiException {
		Double totalRevenue = 0.0;
		for(OrderItemPojo item : items) {
			totalRevenue += item.getQuantity() * item.getSellingPrice();
		}
		return totalRevenue;
	}

	public static ZonedDateTime getCurrentZonedDateWithoutTime(){
		return setTimeToZero(ZonedDateTime.now());
	}
	public static ZonedDateTime setTimeToZero(ZonedDateTime zdt) {
		return zdt.withHour(0).withMinute(0).withSecond(0).withNano(0);
	}


	public DaySalesPojo get(ZonedDateTime date) throws ApiException {
		return getCheck(date);
	}

	public List<DaySalesPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, DaySalesPojo.class);
	}

	public List<DaySalesPojo> getAll(Integer pageNo, Integer pageSize, ZonedDateTime start, ZonedDateTime end) {
		return dao.filterByDate(pageNo, pageSize, start, end);
	}

	private DaySalesPojo getCheck(ZonedDateTime date) throws ApiException {
		DaySalesPojo daySales = dao.select(DaySalesPojo.class, date);
		if (Objects.isNull(daySales))
			throw new ApiException("DaySales does not exist with date: " + date);
		return daySales;
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount(DaySalesPojo.class);
	}

}
