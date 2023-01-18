package com.increff.pos.model;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportForm {
	private ZonedDateTime startDate;
	private ZonedDateTime endDate;
	private String brand;
	private String category;
}
