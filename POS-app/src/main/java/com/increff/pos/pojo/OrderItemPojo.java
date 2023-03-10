package com.increff.pos.pojo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(indexes = {
    @Index(name="IX_orderitempojo_productId", columnList = "productId"),
}, uniqueConstraints = {
    @UniqueConstraint(name="unique_orderId_productId", columnNames = {"orderId", "productId"})
})
@Setter
public class OrderItemPojo extends AbstractPojo{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    @Column(name="orderId", nullable=false)
    private Integer orderId;
    @Column(name="productId", nullable=false)
    private Integer productId;
    @Column(name="quantity", nullable=false)
    private Integer quantity;
    @Column(name="sellingPrice", nullable=false)
    private Double sellingPrice;

    public Double getCost() {
        return quantity * sellingPrice;
    }
    public OrderItemPojo() {
    }
    public OrderItemPojo(Integer productId, Integer quantity, Double sellingPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }
}
