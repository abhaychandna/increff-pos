package com.increff.pos.model.form;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandForm {

	@Size(max = 15, message = "Brand max size 15")
	@NotBlank(message = "Brand may not be empty")
	private String brand;
	@Size(max = 15, message = "Category max size 15")
	@NotBlank(message = "Category may not be empty")
	private String category;
	
}
