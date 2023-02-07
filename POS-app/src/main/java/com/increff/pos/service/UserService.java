package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.data.Role;
import com.increff.pos.pojo.UserPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class UserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserDao dao;
	@Value("${supervisorEmail}")
	private String supervisorEmail;

	public void add(UserPojo user) throws ApiException {
		UserPojo existing = dao.selectByColumn("email", user.getEmail());
		if (Objects.nonNull(existing)) {
			throw new ApiException("User already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(isSupervisor(user.getEmail()) ? Role.supervisor : Role.operator);
		dao.insert(user);
	}

	public UserPojo get(String email) throws ApiException {
		return dao.selectByColumn("email", email);
	}

	public List<UserPojo> getAll() {
		return dao.selectAll();
	}

	private boolean isSupervisor(String email) {
		return email.equals(supervisorEmail); 
	}

}
