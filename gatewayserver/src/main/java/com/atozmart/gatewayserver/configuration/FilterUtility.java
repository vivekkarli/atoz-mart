package com.atozmart.gatewayserver.configuration;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtility {

	public static final String CORRELATION_ID = "atozmart-correlation-id";

	public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
		return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
	}

	public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
		return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
	}

	public String getCorrelationId(HttpHeaders requestHeaders) {

		if (requestHeaders.get(CORRELATION_ID) != null) {
			List<String> list = requestHeaders.get(CORRELATION_ID);
			return list.stream().findFirst().get();
		}
		
		return null;
	}

}
