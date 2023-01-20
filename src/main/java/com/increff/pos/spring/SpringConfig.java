package com.increff.pos.spring;

import java.time.ZonedDateTime;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan("com.increff.pos")
@PropertySources({ //
		@PropertySource(value = "file:./pos.properties", ignoreResourceNotFound = true) //
})
public class SpringConfig {

	@PostConstruct
	public void init() {
		System.out.println("SpringConfig.init()");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("Date in UTC: " + ZonedDateTime.now());
	}

}
