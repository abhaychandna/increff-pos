package com.increff.pos.model.form;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportForm {
	private String startDate;
	private String endDate;
	private String brand;
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
