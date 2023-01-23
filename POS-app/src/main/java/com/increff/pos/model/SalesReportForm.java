package com.increff.pos.model;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportForm {
	private String startDate;
	private String endDate;
	private String brand;
	private String category;
}
