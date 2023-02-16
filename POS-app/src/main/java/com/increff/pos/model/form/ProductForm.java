package com.increff.pos.model.form;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

	@Size(max = 15, message = "Barcode max size 15")
	@NotBlank(message = "Barcode may not be empty")
	private String barcode;
	@Size(max = 15, message = "Brand max size 15")
	@NotBlank(message = "Brand may not be empty")
	private String brand;
	@Size(max = 15, message = "Category max size 15")
	@NotBlank(message = "Category may not be empty")
	private String category;
	@Size(max = 15, message = "Name max size 15")
	@NotBlank(message = "Name may not be empty")
	private String name;
	@NotNull(message = "Mrp may not be empty")
	@Min(value = 0, message = "Mrp should be greater than or equal to 0")
	@Max(value = 100000, message = "Mrp should be less than 100000")
	private Double mrp;

}
