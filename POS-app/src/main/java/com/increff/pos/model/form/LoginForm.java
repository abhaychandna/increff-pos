package com.increff.pos.model.form;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

	@Size(max = 64, message = "Email max size 64")
	@NotBlank(message = "Email may not be empty")
	private String email;
	@Size(max = 64, message = "Password max size 64")
	@NotBlank(message = "Password may not be empty")
	private String password;

	public LoginForm() {
	}
	public LoginForm(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
