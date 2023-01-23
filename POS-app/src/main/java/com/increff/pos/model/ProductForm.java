package com.increff.pos.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

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

}
