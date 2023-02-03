package com.increff.pos.model.data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItemData {
    private String name;
    private String barcode;
    private Integer quantity;
    private Double sellingPrice;
    private Integer productId;
}
