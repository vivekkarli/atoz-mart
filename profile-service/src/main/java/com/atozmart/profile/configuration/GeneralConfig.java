package com.atozmart.profile.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;

@Configuration
public class GeneralConfig {

	@Value("${atozmart.admin.user}")
	private String adminUsername;
	
	@Value("${atozmart.admin.pwd}")
	private String adminPwd;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public RequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor(adminUsername, adminPwd);
	}

}
