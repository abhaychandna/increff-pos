package com.increff.pos.model.form;
import java.util.List;

import com.increff.pos.model.data.InvoiceItemData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceForm {
    private Integer orderId; 
    private String time;
    private List<InvoiceItemData> items;
}
