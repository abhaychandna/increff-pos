package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.increff.pos.util.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.data.Role;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.spring.Properties;

@Service
@Transactional(rollbackOn = ApiException.class)
public class UserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserDao dao;
	
	@Autowired
	private Properties Properties;

	public void add(UserPojo user) throws ApiException {
		UserPojo existing = dao.selectByColumn("email", user.getEmail());
		if (Objects.nonNull(existing)) {
			throw new ApiException("User already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(isSupervisor(user.getEmail()) ? Role.SUPERVISOR : Role.OPERATOR);
		dao.insert(user);
	}

	public UserPojo get(String email) throws ApiException {
		return dao.selectByColumn("email", email);
	}

	public List<UserPojo> getAll() {
		return dao.selectAll();
	}

	private boolean isSupervisor(String email) {
		return email.equals(Properties.getSupervisorEmail()); 
	}

}
