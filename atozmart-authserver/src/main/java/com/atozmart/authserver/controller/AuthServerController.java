package com.atozmart.authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.LoginResponse;
import com.atozmart.authserver.dto.SignUpForm;
import com.atozmart.authserver.service.AuthServerService;
import com.atozmart.authserver.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthServerController {

	private final AuthServerService authServerService;

	private final NotificationService notificationService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginForm loginForm) {
		return authServerService.login(loginForm);
	}

	@PostMapping("/signup")
	public ResponseEntity<LoginResponse> signUp(@Valid @RequestBody SignUpForm signUpForm) {
		return authServerService.signUp(signUpForm);
	}

	@GetMapping("/confirm-email")
	public ResponseEntity<String> confirmEmail(@RequestParam String code) {
		return notificationService.confirmEmail(code);
	}

}
