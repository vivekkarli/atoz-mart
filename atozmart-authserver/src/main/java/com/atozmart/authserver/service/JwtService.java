package com.atozmart.authserver.service;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration-time}")
	private long expirationTime;


	public SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(UserDetails userDetails, Map<String, Object> customClaims) {

		return Jwts.builder()
				.subject(userDetails.getUsername())
				.claims(customClaims)
				.issuedAt(new Date(System.currentTimeMillis()))
				.issuer("atozmart-authserver")
				.expiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(getSignInKey())
				.compact();

	}

	public long getExpirationTime() {
		return expirationTime;
	}

}
