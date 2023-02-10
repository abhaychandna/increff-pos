package com.increff.pos.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static Logger logger = Logger.getLogger(SecurityConfig.class);

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http//
			// Match only these URLs
				.requestMatchers()//
				.antMatchers("/api/**")//
				.antMatchers("/ui/**")//
				.and()
				.authorizeRequests()//				
				.antMatchers(HttpMethod.GET, "/api/brands/**").hasAnyAuthority("OPERATOR","SUPERVISOR")//
				.antMatchers("/api/brands/**").hasAuthority("SUPERVISOR")
				.antMatchers(HttpMethod.GET, "/api/products/**").hasAnyAuthority("OPERATOR","SUPERVISOR")//
				.antMatchers("/api/products/**").hasAuthority("SUPERVISOR")
				.antMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyAuthority("OPERATOR","SUPERVISOR")//
				.antMatchers("/api/inventory/**").hasAuthority("SUPERVISOR")
				.antMatchers("/api/orders/**").hasAnyAuthority("SUPERVISOR", "OPERATOR")//
				.antMatchers("/api/daySales/**").hasAnyAuthority("SUPERVISOR")//
				.antMatchers("/api/reports/**").hasAnyAuthority("SUPERVISOR")//
				.antMatchers("/api/about/**").permitAll()
				.antMatchers("/api/admin/**").hasAuthority("SUPERVISOR")
				.antMatchers("/api/**").hasAnyAuthority("SUPERVISOR", "OPERATOR")
				.antMatchers("/ui/admin/**").hasAuthority("SUPERVISOR")
				.antMatchers("/ui/**").hasAnyAuthority("SUPERVISOR", "OPERATOR")
				.and()
				.csrf().disable()
				.cors().disable();
		logger.info("Configuration complete");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
				"/swagger-ui.html", "/webjars/**");
	}

}