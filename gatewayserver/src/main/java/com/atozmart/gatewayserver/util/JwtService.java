package com.atozmart.gatewayserver.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import com.atozmart.gatewayserver.exception.GatewayServerException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtService {

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
		JwtParser jwtParser = Jwts.parser().verifyWith(getPublicKey()).build();
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public PublicKey getPublicKey() {
		CertificateFactory certificateFactory = null;
		File certFile = null;
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
			certFile = Paths.get(ClassLoader.getSystemResource("atozmart_authserver.cer").toURI()).toFile();
		} catch (Exception e) {
			log.debug("exception at getting certificate: {}", e.getMessage());
			throw new GatewayServerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try (FileInputStream fis = new FileInputStream(certFile)) {
			Certificate certificate = certificateFactory.generateCertificate(fis);
			return certificate.getPublicKey();
		} catch (Exception e) {
			log.debug("exception at extracting public key: {}", e.getMessage());
			throw new GatewayServerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
