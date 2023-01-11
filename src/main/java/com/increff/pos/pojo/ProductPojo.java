package com.increff.pos.pojo;

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
    private int id;
    private String barcode;
    private int brandCategory;
    private String name;
    private double mrp;

}
