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

@Getter
@Setter
@Table(indexes = {
    @Index(name = "IX_productpojo_brand_category", columnList = "brandCategory"),
},  uniqueConstraints = {
    @UniqueConstraint(name = "unique_barcode", columnNames = {"barcode"}),
})
@Entity
public class ProductPojo extends AbstractPojo {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    @Column(name="barcode", nullable=false)
    private String barcode;
    @Column(name="brandCategory", nullable=false)
    private Integer brandCategory;
    @Column(name="name", nullable=false)
    private String name;
    @Column(name="mrp", nullable=false)
    private Double mrp;

    public ProductPojo() {
    }
    public ProductPojo(String barcode, Integer brandCategory, String name, Double mrp) {
        this.barcode = barcode;
        this.brandCategory = brandCategory;
        this.name = name;
        this.mrp = mrp;
    }

}
