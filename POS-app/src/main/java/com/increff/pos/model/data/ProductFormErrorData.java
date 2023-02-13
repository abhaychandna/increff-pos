package com.increff.pos.model.data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFormErrorData {
	private String barcode;
	private String brand;
	private String category;
	private String name;
	private Double mrp;
	private String error;
	public ProductFormErrorData(String barcode, String brand, String category, String name, Double mrp, String error) {
		this.barcode = barcode;
		this.brand = brand;
		this.category = category;
		this.name = name;
		this.mrp = mrp;
		this.error = error;
	}
}
