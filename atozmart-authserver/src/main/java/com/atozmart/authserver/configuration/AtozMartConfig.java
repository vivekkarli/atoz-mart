package com.atozmart.authserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atozmart")
public record AtozMartConfig(

		String baseUrl,

		Long pwdResetTokenExpiry,

		Long jwtTokenExpiry,

		Long emailVerifyTokenExpiry,

		Long cacheExpiry, String keyStorePass, String keyPass) {
}
