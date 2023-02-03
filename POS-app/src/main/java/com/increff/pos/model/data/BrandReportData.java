package com.increff.pos.model.data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandReportData {
	private String brand;
	private String category;
	public BrandReportData(String brand, String category) {
		this.brand = brand;
		this.category = category;
	}
}
