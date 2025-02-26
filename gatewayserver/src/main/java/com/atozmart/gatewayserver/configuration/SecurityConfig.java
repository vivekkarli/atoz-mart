package com.atozmart.gatewayserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityWebFilterChain secuFilterChain(ServerHttpSecurity http) {
		
		http.authorizeExchange(exchanges -> exchanges
				.pathMatchers(HttpMethod.GET).permitAll()
				.pathMatchers("/actuator/**").permitAll()
				.pathMatchers(HttpMethod.DELETE, "/atozmart/catalog/**").hasRole("ADMIN")
				.pathMatchers(HttpMethod.DELETE, "/atozmart/card/**").hasRole("ADMIN"))
		.oauth2ResourceServer(oauth2spec -> oauth2spec
				.jwt(jwtSpec -> jwtSpec
						.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
		
		http.csrf(CsrfSpec::disable);
		
		return http.build();
	}
	
	Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor(){
		
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		
		converter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
		
		return new ReactiveJwtAuthenticationConverterAdapter(converter);
	}

}
