package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPrincipal {

	private Integer id;
	private String email;
	private Role role;

}