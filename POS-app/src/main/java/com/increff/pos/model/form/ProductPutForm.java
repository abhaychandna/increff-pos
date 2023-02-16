package com.increff.pos.model.form;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPutForm {

	@Size(max = 15, message = "Name max size 15")
	@NotBlank(message = "Name may not be empty")
	private String name;
	@NotNull(message = "Mrp may not be empty")
	@Min(value = 0, message = "Mrp should be greater than or equal to 0")
	@Max(value = 100000, message = "Mrp should be less than 100000")
	private Double mrp;

	public ProductPutForm() {
	}
	public ProductPutForm(String name, Double mrp) {
		this.name = name;
		this.mrp = mrp;
	}
}
