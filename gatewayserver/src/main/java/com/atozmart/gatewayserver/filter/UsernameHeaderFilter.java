package com.atozmart.gatewayserver.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.atozmart.gatewayserver.authentication.AtozmartAuthenticationToken;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10) // to run after authentication but before routing
public class UsernameHeaderFilter implements GlobalFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
				.flatMap(authentication -> {

					if (authentication != null && authentication.isAuthenticated()) {
						String username = extractUsername(authentication);
						String email = extractEmail(authentication);

						ServerWebExchange serverWebExchange = exchange.mutate()
								.request(exchange.getRequest().mutate().headers(httpHeaders -> {
									httpHeaders.add("X-Username", username);
									httpHeaders.add("X-User-Email", email);
								}).build()).build();

						return chain.filter(serverWebExchange);
					} else {

						return chain.filter(exchange);
					}
				}).switchIfEmpty(chain.filter(exchange));

	}

	private String extractEmail(Authentication authentication) {

		log.info("authentication: {}", authentication);

		String email = switch (authentication) {
		case AtozmartAuthenticationToken atozmartToken -> atozmartToken.getAuthorizeResponse().email();
		case JwtAuthenticationToken jwtAuthToken -> jwtAuthToken.getToken().getClaimAsString("email");
		case UsernamePasswordAuthenticationToken upat -> (String) upat.getPrincipal();
		default -> null;

		};

		log.info("extracted email: {}", email);
		return email;

	}

	private String extractUsername(Authentication authentication) {

		log.info("authentication: {}", authentication);

		String username = switch (authentication) {
		case AtozmartAuthenticationToken atozmartToken -> atozmartToken.getAuthorizeResponse().username();
		case JwtAuthenticationToken jwtAuthToken -> jwtAuthToken.getToken().getClaimAsString("preferred_username");
		case UsernamePasswordAuthenticationToken upat -> (String) upat.getPrincipal();
		default -> "anonymousUser";

		};

		log.info("extracted username: {}", username);
		return username;

	}

}