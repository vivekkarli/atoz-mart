package com.atozmart.gatewayserver.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.atozmart.gatewayserver.entity.AppUser;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10) // Run after authentication but before routing
public class UsernameHeaderFilter implements GlobalFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
				.flatMap(authentication -> {

					if (authentication != null && authentication.isAuthenticated()) {
						String username = extractusername(authentication);

						ServerWebExchange serverWebExchange = exchange.mutate()
								.request(exchange.getRequest().mutate().header("X-Username", username).build()).build();

						return chain.filter(serverWebExchange);
					} else {

						return chain.filter(exchange);
					}
				}).switchIfEmpty(chain.filter(exchange));

	}

	private String extractusername(Authentication authentication) {

		log.info("authentication: {}", authentication);
		String username = "anonymousUser";

		if (authentication instanceof UsernamePasswordAuthenticationToken upat) {
			AppUser appUser = (AppUser) upat.getPrincipal();
			username = appUser.getUsername();
			log.info("extracted username: {}", username);
		} else if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
			Jwt token = jwtAuthToken.getToken();
			username = token.getClaimAsString("preferred_username");
			log.info("extracted username: {}", username);
		}

		return username;

	}

}