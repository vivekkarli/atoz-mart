package com.atozmart.authserver.function;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AuthserverFunctions {

	@Bean
	public Consumer<String> emailVerificationMailConfirmation() {
		return message -> log.info("mail confirmation: {}", message);

	}

}
