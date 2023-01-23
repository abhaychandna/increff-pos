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
}
