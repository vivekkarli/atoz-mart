package com.atozmart.gatewayserver.configuration;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import com.nimbusds.jwt.JWTParser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@AllArgsConstructor
@Configuration
public class GeneralConfig {

	private final AtozmartConfig atozmartConfig;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ReactiveJwtDecoder dynamicReactiveJwtDecoder() {

		Map<String, String> tokenIssuerJwksMap = atozmartConfig.tokenIssuerJwksMap();
		log.info("tokenIssuerJwksMap: {}", tokenIssuerJwksMap);

		Map<String, ReactiveJwtDecoder> decoderMap = tokenIssuerJwksMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> NimbusReactiveJwtDecoder.withJwkSetUri(entry.getValue()).build()));

		return token -> Mono.justOrEmpty(token).flatMap(t -> {
			try {
				String issuer = JWTParser.parse(t).getJWTClaimsSet().getIssuer();

				ReactiveJwtDecoder selected = decoderMap.get(issuer);

				if (selected == null) {
					return Mono.error(new JwtException("Unknown issuer: " + issuer));
				}

				return selected.decode(t);
			} catch (Exception e) {
				return Mono.error(new JwtException("Invalid token format", e));
			}
		});
	}

}
