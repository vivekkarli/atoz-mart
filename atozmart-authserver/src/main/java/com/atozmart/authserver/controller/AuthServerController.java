package com.atozmart.authserver.controller;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.TokenResponse;
import com.atozmart.authserver.service.AuthServerService;
import com.atozmart.authserver.service.JwtService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthServerController {
	
	
	private AuthServerService authServerService;
	
	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@RequestBody LoginForm loginForm) {
		
		String accessToken = authServerService.login(loginForm);
		
		return ResponseEntity.ok(new TokenResponse(accessToken));
	}
	
	

}
