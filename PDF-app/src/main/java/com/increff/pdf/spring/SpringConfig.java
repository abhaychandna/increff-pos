package com.increff.pdf.spring;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan("com.increff.pdf")
@PropertySources({ //
		@PropertySource(value = "file:./pdf.properties", ignoreResourceNotFound = true) //
})
public class SpringConfig {

	@Value("${server.timezone}")
	private String serverTimezone;

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(serverTimezone));
	}

}
