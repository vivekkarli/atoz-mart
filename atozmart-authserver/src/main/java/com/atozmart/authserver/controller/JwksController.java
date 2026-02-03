package com.atozmart.authserver.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
public class JwksController {

	private final JWKSet jwkSet;

	@GetMapping("/.well-known/jwks.json")
	public Map<String, Object> jwks() {
		log.info("accessed /.well-known/jwks.json endpoint");
		return jwkSet.toPublicJWKSet().toJSONObject();
	}
}
