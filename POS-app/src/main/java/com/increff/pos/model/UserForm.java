package com.increff.pos.model;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {

	@NotBlank(message = "Email may not be empty")
	private String email;
	@NotBlank(message = "Password may not be empty")
	private String password;
	private Role role;

}
