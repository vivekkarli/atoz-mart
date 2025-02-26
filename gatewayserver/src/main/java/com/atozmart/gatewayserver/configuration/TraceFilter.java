package com.atozmart.gatewayserver.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class TraceFilter {

	private FilterUtility filterUtility;

	private static final Logger LOGGER = LoggerFactory.getLogger(TraceFilter.class);

	@Bean
	@Order(1)
	public GlobalFilter requestTraceFilter() {

		return (exchange, chain) -> {

			HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

			if (isCorrelationIdPresent(requestHeaders)) {
				LOGGER.debug("atozmart-correlation-id found in RequestTraceFilter : {}",
						filterUtility.getCorrelationId(requestHeaders));
			} else {
				String correlationID = generateCorrelationId();
				exchange = filterUtility.setCorrelationId(exchange, correlationID);
				LOGGER.debug("atozmart-correlation-id generated in RequestTraceFilter : {}", correlationID);
			}

			return chain.filter(exchange);
		};

	}

	@Bean
	public GlobalFilter responseTraceFilter() {

		return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
			HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
			String correlationId = filterUtility.getCorrelationId(requestHeaders);
			LOGGER.debug("Updated the correlation id to the outbound headers: {}", correlationId);
			exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
		}));

	}

	private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
		return filterUtility.getCorrelationId(requestHeaders) != null;
	}

	private String generateCorrelationId() {
		return java.util.UUID.randomUUID().toString();
	}

}
