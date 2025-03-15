package com.atozmart.gatewayserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.atozmart.gatewayserver.service.CustomTokenAuthenticationConverter;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
@AllArgsConstructor
public class SecurityConfig {

	private final CustomTokenAuthenticationConverter customTokenAuthenticationConverter;
	
	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

		// Create the AuthenticationWebFilter with a ReactiveAuthenticationManager
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(customTokenAuthenticationConverter);

		http.authorizeExchange(exchanges -> exchanges

				// gateway endpoints
				.pathMatchers("/test").permitAll()
				// .pathMatchers("/actuator/**").permitAll()

				// catalog-service endpoints
				.pathMatchers(HttpMethod.DELETE, "/atozmart/catalog/**").hasRole("ADMIN")

				// cart-service endpoints
				.pathMatchers(HttpMethod.DELETE, "/atozmart/cart/**").hasRole("ADMIN")

				// wishlist-service endpoints
				.pathMatchers("/atozmart/wishlist/**").hasAnyRole("USER", "ADMIN")

				.anyExchange().permitAll());

		http.csrf(CsrfSpec::disable);

		http.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

		return http.build();
	}

	@Bean
	public CorsConfiguration corsConfiguration() {

		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");

		return corsConfiguration;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ReactiveAuthenticationManager reactiveAuthenticationManager() {
		return Mono::just;
	}

}
