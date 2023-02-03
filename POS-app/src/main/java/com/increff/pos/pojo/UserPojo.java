package com.increff.pos.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.increff.pos.model.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserPojo extends AbstractPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	@Column(name="role")
	private Role role;

	public UserPojo() {
	}
	public UserPojo(String email, String password, Role role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}
	
}
