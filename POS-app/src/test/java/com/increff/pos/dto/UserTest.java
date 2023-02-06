package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.TestUtil;
import com.increff.pos.service.UserService;

public class UserTest extends AbstractUnitTest {

    @Autowired
    private UserDto userDto;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${supervisorEmail}")
    private String supervisorEmail;

    private String email;
    private String password;
    @Before
    public void init() throws ApiException {
        email = "test@test.com";
        password = "test21i39812asd";
    }

    @Test
    public void testSignup() throws ApiException {
        SignupForm form = testUtil.getSignupFormDto(email, password);
        MockHttpServletRequest request = new MockHttpServletRequest();
        userDto.signup(request, form);
        UserPojo user = userDao.selectByColumn("email", email);
        checkEquals(user, form);
    }

    @Test
    public void testSignupSupervisor() throws ApiException {
        SignupForm form = testUtil.getSignupFormDto(supervisorEmail, password);
        MockHttpServletRequest request = new MockHttpServletRequest();
        userDto.signup(request, form);
        UserPojo user = userService.get(supervisorEmail);
        checkEquals(user, form); 
        assertEquals("supervisor", user.getRole().toString());
    }    

    private void checkEquals(UserPojo user, SignupForm form) {
        assertEquals(user.getEmail(), form.getEmail());
        assertTrue(passwordEncoder.matches(form.getPassword(), user.getPassword()));
    }


}