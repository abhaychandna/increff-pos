package com.increff.pos.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.dto.UserDto;
import com.increff.pos.model.LoginForm;
import com.increff.pos.model.SignupForm;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(path = "/session")
public class LoginController {
	
	@Autowired
	private UserDto dto;

	
	@ApiOperation(value = "Logs in a user")
	@RequestMapping(path = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView login(HttpServletRequest req, LoginForm form) throws ApiException {
		return dto.login(req, form);
	}

	@RequestMapping(path = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().invalidate();
		return new ModelAndView("redirect:/site/login");
	}

	@ApiOperation(value = "Signs up a user")
	@RequestMapping(path = "/signup", method = RequestMethod.POST)
	public ModelAndView signup(HttpServletRequest req, SignupForm form) throws ApiException {
		return dto.signup(req, form);
	}	


}
