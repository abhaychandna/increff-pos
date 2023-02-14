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

    public InvoiceItemData(String name, String barcode, Integer quantity, Double sellingPrice, Integer productId) {
        this.name = name;
        this.barcode = barcode;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.productId = productId;
    }
}
