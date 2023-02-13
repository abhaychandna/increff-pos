package com.increff.pos.model.form;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemForm {
    @Size(max = 255, message = "Barcode max size 255")
    @NotBlank(message = "Barcode may not be empty")
    private String barcode ;
    @NotNull(message = "Quantity may not be empty")
    @Min(value = 1, message = "Quantity should be positive")
    private Integer quantity ; 
    @NotNull(message = "Selling Price may not be empty")
    @Min(value = 0, message = "Selling Price should be greater than or equal to 0")
    private Double  sellingPrice ;

    public OrderItemForm() {
    }
    public OrderItemForm(String barcode, Integer quantity, Double sellingPrice) {
        this.barcode = barcode;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }
}
