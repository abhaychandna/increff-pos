package com.increff.pdf.spring;

import java.time.ZonedDateTime;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

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

	@PostConstruct
	public void init() {
		System.out.println("SpringConfig.init()");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("Date in UTC: " + ZonedDateTime.now());
	}

}
