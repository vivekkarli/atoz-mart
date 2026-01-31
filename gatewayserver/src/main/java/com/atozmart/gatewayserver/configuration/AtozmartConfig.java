package com.atozmart.gatewayserver.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atozmart")
public record AtozmartConfig(AdminDetails admin, CorsConfig cors) {

	public record AdminDetails(String username, String password) {
	}

	public record CorsConfig(List<String> allowedOrigins, List<String> allowedHeaders, List<String> allowedMethods,
			List<String> exposedHeaders) {
	}

}
