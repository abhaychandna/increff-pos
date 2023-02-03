package com.increff.pos.model.data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryFormErrorData {
	private String barcode;
	private Integer quantity;
	private String error;
	public InventoryFormErrorData(String barcode, Integer quantity, String error) {
		this.barcode = barcode;
		this.quantity = quantity;
		this.error = error;
	}

}
