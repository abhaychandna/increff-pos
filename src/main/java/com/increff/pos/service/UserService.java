package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class UserService {

	@Autowired
	private UserDao dao;

	public void add(UserPojo user) throws ApiException {
		normalize(user);
		UserPojo existing = dao.select(user.getEmail());
		if (existing != null) {
			throw new ApiException("User with given email already exists");
		}
		dao.insert(user);
	}

	public UserPojo get(String email) throws ApiException {
		return dao.select(email);
	}

	public List<UserPojo> getAll() {
		return dao.selectAll();
	}

	public void delete(int id) {
		dao.delete(id);
	}

	protected static void normalize(UserPojo user) {
		user.setEmail(user.getEmail().toLowerCase().trim());
		user.setRole(user.getRole().toLowerCase().trim());
	}
}
