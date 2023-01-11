package com.increff.pos.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class InventoryPojo {

	@Id
	@Column(name="id", nullable=false)
	private int id;
	@Column(name="quantity", nullable=false)
	private int quantity;

}
