package com.increff.pos.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class InventoryPojo {

	@Id
	private int id;
	private int quantity;

}
