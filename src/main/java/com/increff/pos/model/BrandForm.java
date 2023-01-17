package com.increff.pos.model;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandForm {

	@NotBlank(message = "Name may not be empty")
	private String brand;
	@NotBlank(message = "Category may not be empty")
	private String category;
	
}
