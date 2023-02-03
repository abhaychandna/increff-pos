package com.increff.pos.util;

import com.increff.pos.model.data.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPrincipal {

	private Integer id;
	private String email;
	private Role role;

}