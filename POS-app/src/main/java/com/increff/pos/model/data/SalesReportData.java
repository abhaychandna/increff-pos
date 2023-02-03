package com.increff.pos.model.data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportData {
	private String brand;
	private String category;
	private Integer quantity;
	private Double revenue;
	public SalesReportData(String brand, String category, Integer quantity, Double revenue) {
		this.brand = brand;
		this.category = category;
		this.quantity = quantity;
		this.revenue = revenue;
	}
}
