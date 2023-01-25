package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class UserService {

	@Autowired
	private UserDao dao;
	@Value("${supervisorEmail}")
	private String supervisorEmail;

	public void add(UserPojo user) throws ApiException {
		UserPojo existing = dao.selectByColumn(UserPojo.class, "email", user.getEmail());
		if (Objects.nonNull(existing)) {
			throw new ApiException("User already exists");
		}
		user.setRole(isSupervisor(user.getEmail()) ? "supervisor" : "operator");
		dao.insert(user);
	}

	private boolean isSupervisor(String email) {
		System.out.println("supervisorEmail: " + supervisorEmail);
		return email.equals(supervisorEmail); 
	}

	public UserPojo get(String email) throws ApiException {
		return dao.selectByColumn(UserPojo.class, "email", email);
	}

	public List<UserPojo> getAll() {
		return dao.selectAll(UserPojo.class);
	}

	public void delete(Integer id) {
		dao.delete(id);
	}
}