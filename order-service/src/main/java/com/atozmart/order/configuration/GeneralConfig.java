package com.atozmart.order.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class GeneralConfig {

	private final AtozmartAdminDetails adminDetails;

	@Bean
	public RequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor(adminDetails.getUsername(), adminDetails.getPassword());
	}

}
