package com.increff.pos.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.increff.pos.util.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class DaySalesService {

	@Autowired
	private DaySalesDao dao;

	public DaySalesPojo add(DaySalesPojo daySales) throws ApiException {
		DaySalesPojo existing = dao.select(daySales.getDate());
		if (Objects.nonNull(existing)) {
			throw new ApiException("DaySales already exists with date: " + daySales.getDate());
		}
		dao.insert(daySales);
		return daySales;
	}

	public DaySalesPojo update(DaySalesPojo daySales) throws ApiException {
		DaySalesPojo existing = dao.select(daySales.getDate());
		if(Objects.isNull(existing)) {
			return add(daySales);
		}
		existing.setInvoicedItemsCount(daySales.getInvoicedItemsCount());
		existing.setInvoicedOrdersCount(daySales.getInvoicedOrdersCount());
		existing.setTotalRevenue(daySales.getTotalRevenue());
		return existing;
	}

	public List<DaySalesPojo> getAll(Integer pageNo, Integer pageSize) {
		return dao.selectAll(pageNo, pageSize);
	}

	public List<DaySalesPojo> getAll(Integer pageNo, Integer pageSize, ZonedDateTime start, ZonedDateTime end) {
		return dao.filterByDate(pageNo, pageSize, start, end);
	}

	public DaySalesPojo getCheck(ZonedDateTime date) throws ApiException {
		DaySalesPojo daySales = dao.select(date);
		if (Objects.isNull(daySales))
			throw new ApiException("DaySales does not exist with date: " + date);
		return daySales;
	}

	public Integer getRecordsCount() {
		return dao.getRecordsCount();
	}

}
