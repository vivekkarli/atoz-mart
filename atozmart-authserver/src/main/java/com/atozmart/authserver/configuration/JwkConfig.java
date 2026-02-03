package com.atozmart.authserver.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class JwkConfig {

	private final AtozMartConfig atozMartConfig;

	@Bean
	public JWKSet jwkSet() throws URISyntaxException, KeyStoreException, IOException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableEntryException {
		File keyFile = Paths.get(ClassLoader.getSystemResource("atozmart_keystore.p12").toURI()).toFile();
		KeyStore keyStore = KeyStore.getInstance(keyFile, atozMartConfig.keyStorePass().toCharArray());

		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
				atozMartConfig.keyAlias(), new KeyStore.PasswordProtection(atozMartConfig.keyPass().toCharArray()));

		RSAPrivateKey privatekey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
		RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

		RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privatekey).keyID(UUID.randomUUID().toString())
				.build();
		return new JWKSet(rsaKey);
	}

}
