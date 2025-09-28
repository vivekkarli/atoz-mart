package com.atozmart.authserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "atozmart")
@Component
@Data
public class AtozMartConfig {

	private String baseUrl;

	private Long pwdResetTokenExpiry;

	private Long jwtTokenExpiry;

	private Long emailVerifyTokenExpiry;

	private String jwtSecretKey;

}
