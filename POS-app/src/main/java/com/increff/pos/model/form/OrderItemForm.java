package com.increff.pos.model.form;
import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemForm {
    @Size(max = 15, message = "Barcode max size 15")
    @NotBlank(message = "Barcode may not be empty")
    private String barcode ;
    @NotNull(message = "Quantity may not be empty")
    @Min(value = 1, message = "Quantity should be positive")
    @Max(value = 1000, message = "Quantity should be less than 1000")
    private Integer quantity ; 
    @NotNull(message = "Selling Price may not be empty")
    @Min(value = 0, message = "Selling Price should be greater than or equal to 0")
    @Max(value = 100000, message = "Selling Price should be less than 100000")
    private Double  sellingPrice ;

    public OrderItemForm() {
    }
    public OrderItemForm(String barcode, Integer quantity, Double sellingPrice) {
        this.barcode = barcode;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }
}
