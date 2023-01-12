package com.increff.pos.pojo;
import javax.persistence.Column;
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
public class OrderItemPojo extends AbstractPojo{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    private Integer id;
    @Column(name="orderId", nullable=false)
    private Integer orderId;
    @Column(name="productId", nullable=false)
    private Integer productId;
    @Column(name="quantity", nullable=false)
    private Integer quantity;
    @Column(name="sellingPrice", nullable=false)
    private Double sellingPrice;

}
