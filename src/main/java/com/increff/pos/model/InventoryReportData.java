package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReportData {
	private String brand;
	private String category;
	private Integer quantity;

	public InventoryReportData(String brand, String category, Integer quantity){
		this.brand = brand;
		this.category = category;
		this.quantity = quantity;
	}
}
