package com.increff.pos.spring;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan("com.increff.pos")
@PropertySources({ //
		@PropertySource(value = "file:./pos.properties", ignoreResourceNotFound = true) //
})
public class SpringConfig {

	@Value("${server.timezone}")
	private String serverTimezone;

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(serverTimezone));
	}

}
