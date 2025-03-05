package com.atozmart.gatewayserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
		
		http.authorizeExchange(exchanges -> exchanges
				
				// gateway endpoints
				.pathMatchers("/actuator/**").permitAll()
				.pathMatchers(HttpMethod.POST, "/signup").permitAll()
				.pathMatchers(HttpMethod.DELETE, "/remove-account").authenticated() // user need to be logged-in to delete his/her account
				
				// catalog-service endpoints
				.pathMatchers(HttpMethod.DELETE, "/atozmart/catalog/**").hasRole("ADMIN")
				
				// cart-service endpoints
				.pathMatchers(HttpMethod.DELETE, "/atozmart/cart/**").hasRole("ADMIN")
				
				// wishlist-service endpoints
				.pathMatchers("/atozmart/wishlist/**").hasAnyRole("USER","ADMIN")
				
				.anyExchange().permitAll());
		
		http.oauth2ResourceServer(oauth2spec -> oauth2spec
				.jwt(jwtSpec -> jwtSpec
						.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
		
		http.csrf(CsrfSpec::disable);
		
		http.httpBasic(Customizer.withDefaults());
		
		return http.build();
	}
	
	private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor(){
		
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		
		converter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
		
		return new ReactiveJwtAuthenticationConverterAdapter(converter);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
}
