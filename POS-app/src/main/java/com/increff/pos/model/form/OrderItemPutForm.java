package com.increff.pos.model.form;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemPutForm {
    @NotNull(message = "Quantity may not be empty")
    @Min(value = 0, message = "Quantity should be greater than or equal to 0")
    private Integer quantity ; 
    @NotNull(message = "Selling Price may not be empty")
    @Min(value = 0, message = "Selling Price should be greater than or equal to 0")
    private Double  sellingPrice ;
}
