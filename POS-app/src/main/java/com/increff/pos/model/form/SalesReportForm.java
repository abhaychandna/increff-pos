package com.increff.pos.model.form;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class SalesReportForm {
	private String startDate;
	private String endDate;
	@Size(max = 15, message = "Brand max size 15")
	private String brand;
	@Size(max = 15, message = "Category max size 15")
	private String category;

	public SalesReportForm() {
	}
	public SalesReportForm(String startDate, String endDate, String brand, String category) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.brand = brand;
		this.category = category;
	}
	public SalesReportForm(String startDate, String endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

}
