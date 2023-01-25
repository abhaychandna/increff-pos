package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryFormErrorData {
	private String error;
	private String barcode;
	private Integer quantity;
	public InventoryFormErrorData(String barcode, Integer quantity, String error) {
		this.barcode = barcode;
		this.quantity = quantity;
		this.error = error;
	}

}
