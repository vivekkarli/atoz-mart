package com.atozmart.gatewayserver.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.atozmart.gatewayserver.util.RoleConverter;

import reactor.core.publisher.Mono;

/*
 * This class is used to apply GrantedAuthories to Authentication Object.
 * GrantedAuthories are derived from RoleConverter class
 */
@Component
public class CustomTokenAuthenticationConverter implements ServerAuthenticationConverter {

	private final ReactiveJwtDecoder jwtDecoder;

	private final JwtAuthenticationConverter jwtAuthenticationConverter;

	public CustomTokenAuthenticationConverter(ReactiveJwtDecoder jwtDecoder) {
		this.jwtDecoder = jwtDecoder;
		this.jwtAuthenticationConverter = new JwtAuthenticationConverter();
		this.jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new RoleConverter());
	}

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		String token = extractToken(exchange.getRequest());
		if (token == null) {
			return Mono.empty();
		}

		return jwtDecoder.decode(token).flatMap(jwt -> {
			return Mono.just(jwtAuthenticationConverter.convert(jwt));
		});
	}

	private String extractToken(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
