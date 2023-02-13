package com.increff.pos.model.form;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandForm {

	@Size(max = 255, message = "Brand max size 255")
	@NotBlank(message = "Brand may not be empty")
	private String brand;
	@Size(max = 255, message = "Category max size 255")
	@NotBlank(message = "Category may not be empty")
	private String category;
	
}
