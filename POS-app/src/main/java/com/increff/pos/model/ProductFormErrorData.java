package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFormErrorData {
/*
 * 
	@NotBlank(message = "Barcode may not be empty")
	private String barcode;
	@NotBlank(message = "Brand may not be empty")
	private String brand;
	@NotBlank(message = "Category may not be empty")
	private String category;
	@NotBlank(message = "Name may not be empty")
	private String name;
	@NotNull(message = "Mrp may not be empty")
	@Min(value = 0, message = "Mrp should be greater than or equal to 0")
	private Double mrp;
 */
	private String error;
	private String barcode;
	private String brand;
	private String category;
	private String name;
	private Double mrp;
	public ProductFormErrorData(String barcode, String brand, String category, String name, Double mrp, String error) {
		this.barcode = barcode;
		this.brand = brand;
		this.category = category;
		this.name = name;
		this.mrp = mrp;
		this.error = error;
	}

}
