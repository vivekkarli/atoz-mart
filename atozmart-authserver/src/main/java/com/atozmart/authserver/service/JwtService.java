package com.atozmart.authserver.service;

import java.io.File;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.configuration.AtozMartConfig;
import com.atozmart.authserver.dto.AppUserDto;
import com.atozmart.authserver.exception.AuthServerException;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class JwtService {

	private final AtozMartConfig atozMartConfig;

	public String generateToken(AppUserDto appUser, Map<String, Object> customClaims) {

		return Jwts.builder().subject(appUser.getUsername()).claims(customClaims)
				.issuedAt(new Date(System.currentTimeMillis())).issuer("atozmart-authserver")
				.expiration(new Date(System.currentTimeMillis() + atozMartConfig.jwtTokenExpiry()))
				.signWith(getPrivateKey()).compact();

	}

	private Key getPrivateKey() {
		try {
			File keyFile = Paths.get(ClassLoader.getSystemResource("atozmart_keystore.p12").toURI()).toFile();
			KeyStore keyStore = KeyStore.getInstance(keyFile, atozMartConfig.keyStorePass().toCharArray());
			return keyStore.getKey("atozmart_authserver", atozMartConfig.keyPass().toCharArray());
		} catch (Exception e) {
			log.debug("exception at generating token: {}", e.getMessage());
			throw new AuthServerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
