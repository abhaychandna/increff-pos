package com.increff.pos.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.data.UserPrincipal;
import com.increff.pos.spring.Properties;
import com.increff.pos.util.SecurityUtil;

@Controller
public abstract class AbstractUiController {

	@Autowired
	private InfoData info;

	@Autowired
	private Properties Properties;

	protected ModelAndView mav(String page) {

		UserPrincipal principal = SecurityUtil.getPrincipal();

		info.setEmail(Objects.isNull(principal) ? "" : principal.getEmail());
		info.setRole(Objects.isNull(principal) ? "" : String.valueOf(principal.getRole()));
		if (!info.getHasError()) info.setMessage("");
		else info.setHasError(false);

		ModelAndView mav = new ModelAndView(page);
		mav.addObject("info", info);
		mav.addObject("baseUrl", Properties.getAppBaseUrl());
		return mav;
	}

}
