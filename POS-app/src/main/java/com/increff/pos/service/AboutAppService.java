package com.increff.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.spring.Properties;

@Service
public class AboutAppService {

	@Autowired
	private Properties properties;
	
	public String getName() {
		return properties.getAppName();
	}

	public String getVersion() {
		return properties.getAppVersion();
	}

}
