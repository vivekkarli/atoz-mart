package com.atozmart.gatewayserver.configuration;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10) // Run after authentication but before routing
public class UsernameHeaderFilter implements GlobalFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		return exchange.getPrincipal().filter(JwtAuthenticationToken.class::isInstance)
				.cast(JwtAuthenticationToken.class).map(jwtAuth -> {
					// Get the JWT (access token) from the authentication object
					Jwt jwt = jwtAuth.getToken();

					// Extract the username from the access token
					String username = jwt.getClaimAsString("preferred_username");
					log.info("extracted username: {}", username);

					if (username == null)
						return exchange;

					return exchange.mutate()
							.request(exchange.getRequest().mutate().header("X-Username", username).build()).build();

				}).defaultIfEmpty(exchange).flatMap(chain::filter);

	}

}