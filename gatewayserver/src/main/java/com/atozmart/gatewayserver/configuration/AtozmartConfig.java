package com.atozmart.gatewayserver.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atozmart")
public record AtozmartConfig(AuthDetails auth, AdminDetails admin, CorsConfig cors) {

	public record AdminDetails(String username, String password) {
	}

	public record AuthDetails(String authorizeEndpoint, String issuer) {
	}

	public record CorsConfig(List<String> allowedOrigins, List<String> allowedHeaders, List<String> allowedMethods,
			List<String> exposedHeaders) {
	}

}

/*
 * atozmart: auth: authorize-endpoint: http://localhost:8074/admin/authorize
 * issuer: atozmart-authserver admin: username: atozmart_gatewayserver
 * allowedOrigins: - 'http://localhost:3000' - 'http://localhost:5173'
 */
