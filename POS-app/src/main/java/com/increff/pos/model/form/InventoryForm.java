package com.increff.pos.model.form;
import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryForm {

	@Size(max = 15, message = "Barcode max size 15")
	@NotBlank(message = "Barcode may not be empty")
	private String barcode;
	@NotNull(message = "Quantity may not be empty")
	@Min(value = 0, message = "Quantity should be positive")
	@Max(value = 10000, message = "Quantity should be less than 10000")
	private Integer quantity;

	public InventoryForm() {
	}
	public InventoryForm(String barcode, Integer quantity) {
		this.barcode = barcode;
		this.quantity = quantity;
	}
}
