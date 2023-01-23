package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryFormErrorData {
	private String error;
	private String barcode;
	private Integer quantity;

}
