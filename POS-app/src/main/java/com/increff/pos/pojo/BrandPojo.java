package com.increff.pos.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(
  uniqueConstraints = {
	@UniqueConstraint(name="unique_brand_category", columnNames = {"brand", "category"})
}
)
@Entity
public class BrandPojo extends AbstractPojo {
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
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
