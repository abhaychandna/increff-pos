package com.increff.pos.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.data.Role;
import com.increff.pos.pojo.UserPojo;

public class UserServiceTest extends AbstractUnitTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    @Value("${supervisorEmail}")
    private String supervisorEmail;

    private String email;
    private String password;
    @Before
    public void init() throws ApiException {
        email = "test@test.com";
        password = "testPassword";
    }

    @Test
    public void testAdd() throws ApiException {
        UserPojo user = new UserPojo(email, password, Role.operator);
        userService.add(user);
        UserPojo userGet = userDao.select(user.getId());
        assertEquals(user, userGet);
    }

    @Test
    public void testGet() throws ApiException {
        UserPojo user = new UserPojo(email, password, Role.operator);
        userDao.insert(user);
        UserPojo userGet = userService.get(email);
        assertEquals(user, userGet);
    }

    @Test
    public void testGetAll() throws ApiException {
        UserPojo user = new UserPojo(email, password, Role.operator);
        userDao.insert(user);
        UserPojo user2 = new UserPojo("email2@gmail.com", "password", Role.supervisor);
        userDao.insert(user2);
        List<UserPojo> userGet = userService.getAll();
        assertEquals(user, userGet.get(0));
        assertEquals(user2, userGet.get(1));
    }
}