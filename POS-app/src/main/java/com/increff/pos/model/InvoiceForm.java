package com.increff.pos.model;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceForm {
    private Integer orderId; 
    private String time;
    private List<InvoiceItemData> items;
}
