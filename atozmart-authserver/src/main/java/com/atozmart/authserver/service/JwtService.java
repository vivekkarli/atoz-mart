package com.atozmart.authserver.service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.entity.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

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

	public String generateToken(AppUser appUser, Map<String, Object> customClaims) {

		return Jwts.builder()
				.subject(appUser.getUsername())
				.claims(customClaims)
				.issuedAt(new Date(System.currentTimeMillis()))
				.issuer("atozmart-authserver")
				.expiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(getSignInKey()).compact();

	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		JwtParser jwtParser = Jwts.parser().verifyWith(getSignInKey()).build();
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

}
