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
	@Index(name="brandCategoryIndex", columnList = "brand, category"),
  })
@Entity
public class BrandPojo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false)
	private int id;
	@Column(name="brand", nullable=false)
    private String brand;
	@Column(name="category", nullable=false)
    private String category;
}
