package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchForm extends ProductForm {
	private Integer id;
    private String barcode;
    private Integer brandCategory;
    private String name;
}
