package com.increff.pos.model.form;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupForm {

	@Size(max = 15, message = "Email max size 15")
	@NotBlank(message = "Email may not be empty")
	private String email;
	@Size(max = 15, message = "Password max size 15")
	@NotBlank(message = "Password may not be empty")
	private String password;

	public SignupForm() {
	}
	public SignupForm(String email, String password) {
		this.email = email;
		this.password = password;
	}

}
