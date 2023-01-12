package com.increff.pos.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemPutForm {
    private Integer quantity ; 
    private Double  sellingPrice ;
}
