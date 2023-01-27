package com.increff.pos.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String email;
	private String password;
	private String role;

	public UserPojo() {
	}
	public UserPojo(String email, String password, String role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}
	
}
