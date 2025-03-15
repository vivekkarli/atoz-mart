package com.atozmart.gatewayserver.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.atozmart.gatewayserver.configuration.KeyCloakRoleConverter;
import com.atozmart.gatewayserver.dto.AuthorizeResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomTokenAuthenticationConverter implements ServerAuthenticationConverter {

	private final WebClient webClient;
	
	private final ReactiveJwtDecoder jwtDecoder;
	
	private final JwtAuthenticationConverter jwtAuthenticationConverter;

	@Value("${atozmart.auth.authorize-endpoint}")
	private String atozmartAuthorizeUrl;

	public CustomTokenAuthenticationConverter(WebClient.Builder webClientBuilder, ReactiveJwtDecoder jwtDecoder) {
		this.webClient = webClientBuilder.build();
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
				}).onErrorMap(e -> new OAuth2AuthenticationException("Invalid Keycloak token: " + e.getMessage()));
	}

	// Validate atozmartAuthServer token by calling /authorize endpoint
	private Mono<Authentication> validateAtozmartToken(String token) {
		  return webClient.get().uri(atozmartAuthorizeUrl + "?token=" + token)
				.retrieve()
				.onStatus( s -> s.is4xxClientError() , response -> Mono.error(new OAuth2AuthenticationException("Invalid atozmart token")))
				.toEntity(AuthorizeResponse.class)
				.flatMap(responseEntity -> {
					AuthorizeResponse response = responseEntity.getBody();
					if (response.valid()) {
						log.info("AtozmartAuthorizeResponse: {}", response);
						List<GrantedAuthority> authorities = extractedAuthoritiesFrom(response.roles());
						return Mono.just(new CustomAtozmartAuthentication(token, authorities))
								.cast(Authentication.class);
					} else {
						return Mono.error(new OAuth2AuthenticationException("Invalid atozmart token"));
					}
				})
				.onErrorMap(e -> new OAuth2AuthenticationException("Error calling atozmartAuthServer: " + e.getMessage()));
	}

	private List<GrantedAuthority> extractedAuthoritiesFrom(List<String> roles) {
		if (roles.isEmpty())
			return Collections.emptyList();
		
		return roles.stream()
				.map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

	}

}
