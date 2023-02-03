package com.increff.pos.model.data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandFormErrorData {
    private String brand;
	private String category;
	private String error;
	public BrandFormErrorData(String brand, String category, String error) {
		this.brand = brand;
		this.category = category;
		this.error = error;
	}
}
