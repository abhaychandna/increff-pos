package com.increff.pos.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class InventoryPojo extends AbstractPojo{

	@Id
	@Column(name="id", nullable=false)
	private Integer productId;
	@Column(name="quantity", nullable=false)
	private Integer quantity;

	public InventoryPojo() {
	}
	public InventoryPojo(Integer quantity) {
		this.quantity = quantity;
	}

}
