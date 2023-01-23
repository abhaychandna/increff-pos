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
				.antMatchers(HttpMethod.GET, "/api/brands/**").hasAnyAuthority("operator","supervisor")//
				.antMatchers("/api/brands/**").hasAuthority("supervisor")
				.antMatchers(HttpMethod.GET, "/api/products/**").hasAnyAuthority("operator","supervisor")//
				.antMatchers("/api/products/**").hasAuthority("supervisor")
				.antMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyAuthority("operator","supervisor")//
				.antMatchers("/api/inventory/**").hasAuthority("supervisor")
				.antMatchers("/api/orders/**").hasAnyAuthority("operator","supervisor")//
				.antMatchers("/api/daySaless/**").hasAnyAuthority("supervisor")//
				.antMatchers("/api/reports/**").hasAnyAuthority("supervisor")//
				.antMatchers("/api/about/**").permitAll()
				.antMatchers("/api/admin/**").hasAuthority("supervisor")
				.antMatchers("/api/**").hasAnyAuthority("supervisor", "operator")
				.antMatchers("/ui/admin/**").hasAuthority("supervisor")
				.antMatchers("/ui/**").hasAnyAuthority("supervisor", "operator")
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