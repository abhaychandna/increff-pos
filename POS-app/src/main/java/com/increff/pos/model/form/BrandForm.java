package com.increff.pos.model.form;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandForm {

	@NotBlank(message = "Brand may not be empty")
	private String brand;
	@NotBlank(message = "Category may not be empty")
	private String category;
	
}
