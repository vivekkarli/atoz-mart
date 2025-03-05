package com.atozmart.gatewayserver.configuration;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class RandomFilter implements GlobalFilter{

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		SecurityContext context = SecurityContextHolder.getContext();
		
		if(context == null);
		
		
		return null;
	}

	
	
}
