package com.atozmart.authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.authserver.dto.AuthorizeResponse;
import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.LoginResponse;
import com.atozmart.authserver.dto.SignUpForm;
import com.atozmart.authserver.service.AuthServerService;
import com.atozmart.authserver.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthServerController {

	private final AuthServerService authServerService;

	private final NotificationService notificationService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginForm loginForm) {
		return authServerService.login(loginForm);
	}

	@PostMapping("/signup")
	public ResponseEntity<LoginResponse> signUp(@Valid @RequestBody SignUpForm signUpForm) {
		return authServerService.signUp(signUpForm);
	}

	@GetMapping("/verify-email")
	public ResponseEntity<String> verifyEmail(@RequestParam String code) {
		return notificationService.verifyEmail(code);
	}

	@GetMapping("/authorize")
	public ResponseEntity<AuthorizeResponse> authorize(@RequestParam(name = "token") String accesstoken) {
		log.info("accesstoken: {}", accesstoken);
		return authServerService.authorizeToken(accesstoken);
	}

}
