package com.increff.pos.model.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

	@Size(max = 255, message = "Barcode max size 255")
	@NotBlank(message = "Barcode may not be empty")
	private String barcode;
	@Size(max = 255, message = "Brand max size 255")
	@NotBlank(message = "Brand may not be empty")
	private String brand;
	@Size(max = 255, message = "Category max size 255")
	@NotBlank(message = "Category may not be empty")
	private String category;
	@Size(max = 255, message = "Name max size 255")
	@NotBlank(message = "Name may not be empty")
	private String name;
	@NotNull(message = "Mrp may not be empty")
	@Min(value = 0, message = "Mrp should be greater than or equal to 0")
	private Double mrp;

}
