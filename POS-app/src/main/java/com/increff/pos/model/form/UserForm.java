package com.increff.pos.model.form;
import javax.validation.constraints.NotBlank;

import com.increff.pos.model.data.Role;

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
