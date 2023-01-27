package com.increff.pos.model;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

	@NotBlank(message = "Email may not be empty")
	private String email;
	@NotBlank(message = "Password may not be empty")
	private String password;

	public LoginForm() {
	}
	public LoginForm(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
