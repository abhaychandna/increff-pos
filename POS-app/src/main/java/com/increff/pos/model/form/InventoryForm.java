package com.increff.pos.model.form;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryForm {

	@Size(max = 255, message = "Barcode max size 255")
	@NotBlank(message = "Barcode may not be empty")
	private String barcode;
	@NotNull(message = "Quantity may not be empty")
	@Min(value = 0, message = "Quantity should be positive")
	private Integer quantity;

	public InventoryForm() {
	}
	public InventoryForm(String barcode, Integer quantity) {
		this.barcode = barcode;
		this.quantity = quantity;
	}
}
