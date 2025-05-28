package com.atozmart.gatewayserver.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {

		return builder.routes()
				.route(p -> p.path("/atozmart/authserver/**")
						.filters(f -> f.rewritePath("atozmart/authserver/(?<segment>.*)", "/${segment}"))
						.uri("lb://ATOZMART-AUTHSERVER"))
				.route(p -> p.path("/atozmart/catalog/**")
						.filters(f -> f.rewritePath("atozmart/catalog/(?<segment>.*)", "/${segment}"))
						.uri("lb://CATALOG-SERVICE"))
				.route(p -> p.path("/atozmart/cart/**")
						.filters(f -> f.rewritePath("atozmart/cart/(?<segment>.*)", "/${segment}"))
						.uri("lb://CART-SERVICE"))
				.route(p -> p.path("/atozmart/wishlist/**")
						.filters(f -> f.rewritePath("atozmart/wishlist/(?<segment>.*)", "/${segment}"))
						.uri("lb://WISHLIST-SERVICE"))
				.route(p -> p.path("/atozmart/order/**")
						.filters(f -> f.rewritePath("atozmart/order/(?<segment>.*)", "/${segment}"))
						.uri("lb://ORDER-SERVICE"))
				.route(p -> p.path("/atozmart/profile/**")
						.filters(f -> f.rewritePath("atozmart/profile/(?<segment>.*)", "/${segment}"))
						.uri("lb://PROFILE-SERVICE"))
				.build();

	}

}
