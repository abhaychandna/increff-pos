package com.increff.pos.model;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement
public class InvoiceItemForm {
    private String name;
    private String barcode;
    private Integer quantity;
    private Double sellingPrice;
    private Integer productId;
}
