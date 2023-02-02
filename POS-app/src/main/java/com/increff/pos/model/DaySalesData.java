package com.increff.pos.model;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaySalesData {
	ZonedDateTime date;
	Integer invoicedItemsCount;
	Integer invoicedOrdersCount;
	Double totalRevenue;
	
	public DaySalesData() {
	}
	public DaySalesData(ZonedDateTime date, Integer invoicedItemsCount, Integer invoicedOrdersCount, Double totalRevenue) {
		this.date = date;
		this.invoicedItemsCount = invoicedItemsCount;
		this.invoicedOrdersCount = invoicedOrdersCount;
		this.totalRevenue = totalRevenue;
	}
}
