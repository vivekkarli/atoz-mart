package com.atozmart.authserver.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.dto.LoginForm;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServerService {

	private UserDetailsService userDetailsService;

	private JwtService jwtService;
	
	private PasswordEncoder passwordEncoder;

	public String login(LoginForm loginForm) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(loginForm.username());
		
		if(userDetails == null || !passwordEncoder.matches(loginForm.password(), userDetails.getPassword()))
			throw new BadCredentialsException("invalid credentials");

		Map<String, Object> customClaims = new HashMap<>();
		customClaims.put("preferred_username", userDetails.getUsername());

		return jwtService.generateToken(userDetails, customClaims);

	}

}
