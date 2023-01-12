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

@Getter
@Setter
@Table(indexes = {
    @Index(name = "brandCategoryIdIndex", columnList = "brandCategory"),
    @Index(name = "barcodeIndex", columnList = "barcode", unique = true),
  })
@Entity
public class ProductPojo {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    private Integer id;
    @Column(name="barcode", nullable=false)
    private String barcode;
    @Column(name="brandCategory", nullable=false)
    private Integer brandCategory;
    @Column(name="name", nullable=false)
    private String name;
    @Column(name="mrp", nullable=false)
    private double mrp;

}
