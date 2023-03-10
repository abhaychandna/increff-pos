package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.models.Model;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.UserDao;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.TestUtil;
import org.springframework.web.servlet.ModelAndView;

public class UserTest extends AbstractUnitTest {

    @Autowired
    private UserDto userDto;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TestUtil testUtil;

    private String email;
    private String password;
    @Before
    public void init() throws ApiException {
        email = "test@test.com";
        password = "test21i39812asd";
    }

    @Test
    public void testSignup() throws ApiException {
        SignupForm form = testUtil.getSignupForm(email, password);
        MockHttpServletRequest request = new MockHttpServletRequest();
        userDto.signup(request, form);
        UserPojo user = userDao.selectByColumn("email", email);
        testUtil.checkEquals(user, form);
    }

    @Test
    public void testSignupSupervisor() throws ApiException {
        SignupForm form = testUtil.getSignupForm(testUtil.getProperties().getSupervisorEmail() , password);
        MockHttpServletRequest request = new MockHttpServletRequest();
        userDto.signup(request, form);
        UserPojo user = userService.get(testUtil.getProperties().getSupervisorEmail());
        testUtil.checkEquals(user, form);
        assertEquals("SUPERVISOR", user.getRole().toString());
    }

    @Test
    public void testLogin() throws ApiException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        userDto.signup(request, testUtil.getSignupForm(email, password));
        ModelAndView mav = userDto.login(request, testUtil.getLoginForm(email, password));
        assertEquals("redirect:/ui/home", mav.getViewName());
    }

    @Test
    public void testLogout() throws ApiException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        userDto.signup(request, testUtil.getSignupForm(email, password));
        assertEquals("redirect:/ui/home", userDto.login(request, testUtil.getLoginForm(email, password)).getViewName());
        ModelAndView mav = userDto.logout(request);
        assertEquals("redirect:/site/login", mav.getViewName());
    }

    @Test
    public void testSignupExisting() throws ApiException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        userDto.signup(request, testUtil.getSignupForm(email, password));
        ModelAndView mav = userDto.signup(request, testUtil.getSignupForm(email, password));
        assertEquals("redirect:/site/signup", mav.getViewName());
    }

}