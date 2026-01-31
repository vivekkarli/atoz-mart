package com.atozmart.gatewayserver.authentication;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.atozmart.gatewayserver.configuration.KeyCloakRoleConverter;
import com.atozmart.gatewayserver.dto.AuthorizeResponse;
import com.atozmart.gatewayserver.exception.GatewayServerException;
import com.atozmart.gatewayserver.util.JwtService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomTokenAuthenticationConverter implements ServerAuthenticationConverter {

	private final JwtService jwtService;

	private final ReactiveJwtDecoder jwtDecoder;

	private final JwtAuthenticationConverter jwtAuthenticationConverter;

	public CustomTokenAuthenticationConverter(ReactiveJwtDecoder jwtDecoder) {
		this.jwtService = new JwtService();
		this.jwtDecoder = jwtDecoder;
		this.jwtAuthenticationConverter = new JwtAuthenticationConverter();
		this.jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
	}

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		String token = extractToken(exchange.getRequest());
		if (token == null) {
			return Mono.empty();
		}

		// Step 1: Try to validate as a Keycloak token (JWT)
		return validateKeycloakToken(token).onErrorResume(OAuth2AuthenticationException.class, e -> {
			// Step 2: If Keycloak validation fails, try atozmartAuthServer
			return validateAtozmartToken(token);
		});
	}

	private String extractToken(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	// Validate Keycloak token using ReactiveJwtDecoder
	private Mono<Authentication> validateKeycloakToken(String token) {
		return jwtDecoder.decode(token) // Decode the JWT token
				.flatMap(jwt -> {
					// Convert Jwt to Authentication (returns Mono<AbstractAuthenticationToken>)
					Mono<? extends Authentication> authentication = Mono.just(jwtAuthenticationConverter.convert(jwt));
					return authentication.cast(Authentication.class);
				}).onErrorMap(e -> {
					log.info("validateKeycloakToken exception: {}", e.getMessage());
					return new OAuth2AuthenticationException("Invalid Keycloak token: " + e.getMessage());
				});
	}

	// Validate atozmartAuthServer token by calling /authorize end point
	private Mono<Authentication> validateAtozmartToken(String token) {

		if (jwtService.isTokenExpired(token))
			throw new GatewayServerException("token expired", HttpStatus.UNAUTHORIZED);

		List<String> roles = (List<String>) jwtService.extractClaim(token, (claims) -> claims.get("roles"));
		String username = (String) jwtService.extractClaim(token, (claims) -> claims.get("preferred_username"));
		String email = (String) jwtService.extractClaim(token, (claims) -> claims.get("email"));
		Date expiration = jwtService.extractExpiration(token);

		List<GrantedAuthority> authorities = extractedAuthoritiesFrom(roles);

		AuthorizeResponse response = new AuthorizeResponse(username, email, expiration, roles);
		log.info("AuthorizeResponse: {}", response);

		return Mono.just(new AtozmartAuthenticationToken(response, null, authorities)).cast(Authentication.class);

	}

	private List<GrantedAuthority> extractedAuthoritiesFrom(List<String> roles) {
		if (roles.isEmpty())
			return Collections.emptyList();

		return roles.stream().map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

	}

}
