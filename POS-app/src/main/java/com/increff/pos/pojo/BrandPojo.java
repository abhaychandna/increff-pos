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
	@Index(name="UX_brandpojo_brand_category", columnList = "brand, category"),
  })
@Entity
public class BrandPojo {
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false)
	private Integer id;
	@Column(name="brand", nullable=false)
    private String brand;
	@Column(name="category", nullable=false)
    private String category;

	public BrandPojo() {
	}
	public BrandPojo(String brand, String category) {
		this.brand = brand;
		this.category = category;
	}
}
