package com.atozmart.authserver.util;

import java.net.URI;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AuthServerUtil {
	
	public String getEncodedToken(String token) {
		var encodedTokenBytes = Base64.getEncoder().encode(token.getBytes());
		return new String(encodedTokenBytes);
	}

	public String getDecodedToken(String encodedToken) {
		var decodedTokenBytes = Base64.getDecoder().decode(encodedToken.getBytes());
		return new String(decodedTokenBytes);
	}

	public URI getResetLink(String baseUrl, String encodedToken) {
		return UriComponentsBuilder.fromUriString(baseUrl).path("reset-password").queryParam("token", encodedToken)
				.build().toUri();
	}

	public URI getVerifyLink(String baseUrl, String encodedToken) {
		return UriComponentsBuilder.fromUriString(baseUrl).path("confirm-email").queryParam("token", encodedToken)
				.build().toUri();
	}

}
