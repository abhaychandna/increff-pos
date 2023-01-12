package com.increff.pos.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
		Date date = getDateWithoutTimeUsingCalendar();
		daySales.setDate(date);
		daySales.setInvoicedItemsCount(items.size());
		daySales.setInvoicedOrdersCount(1);
		daySales.setTotalRevenue(totalRevenue(items));
		
		return dao.insert(daySales);
	}

	public DaySalesPojo update(List<OrderItemPojo> items) throws ApiException {
		DaySalesPojo daySales = new DaySalesPojo();
		try{
			Date date = getDateWithoutTimeUsingCalendar();
			daySales = getCheck(date);
		}
		catch(ApiException e){
			return add(items);
		}
		daySales.setInvoicedItemsCount(daySales.getInvoicedItemsCount() + items.size());
		daySales.setInvoicedOrdersCount(daySales.getInvoicedOrdersCount() + 1);
		daySales.setTotalRevenue(daySales.getTotalRevenue() + totalRevenue(items));
		dao.update(daySales);
		
		return daySales;
	}

	public Double totalRevenue(List<OrderItemPojo> items) throws ApiException {
		Double totalRevenue = 0.0;
		for(OrderItemPojo item : items) {
			totalRevenue += item.getQuantity() * item.getSellingPrice();
		}
		return totalRevenue;
	}

	public static Date getDateWithoutTimeUsingCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	public DaySalesPojo get(Date date) throws ApiException {
		return getCheck(date);
	}

	public List<DaySalesPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize, DaySalesPojo.class);
	}

	private DaySalesPojo getCheck(Date date) throws ApiException {
		DaySalesPojo daySales = dao.select(DaySalesPojo.class, date);
		if (daySales == null)
			throw new ApiException("DaySales does not exist with date: " + date);
		return daySales;
	}


}
