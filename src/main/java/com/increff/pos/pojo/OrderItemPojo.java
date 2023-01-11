package com.increff.pos.pojo;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(indexes = {
    @Index(name="orderIdIndex", columnList = "orderId"),
    @Index(name="productIdIndex", columnList = "productId"),
})
@Setter
public class OrderItemPojo {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double sellingPrice;

}
