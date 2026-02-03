package com.atozmart.gatewayserver.configuration;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atozmart")
public record AtozmartConfig(AdminDetails admin, CorsConfig cors, Map<String, String> tokenIssuerJwksMap) {

	public record AdminDetails(String username, String password) {
	}

	public record CorsConfig(List<String> allowedOrigins, List<String> allowedHeaders, List<String> allowedMethods,
			List<String> exposedHeaders) {
	}

}
