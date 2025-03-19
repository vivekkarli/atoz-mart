package com.atozmart.authserver.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
public class AuthServerController {

	private AuthServerService authServerService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginForm loginForm) {

		String accessToken = authServerService.login(loginForm);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("X-Access-Token", accessToken);

		return new ResponseEntity<>(new LoginResponse("logged in successfully"), httpHeaders, HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<LoginResponse> signUp(@RequestBody SignUpForm signUpForm) {
		authServerService.signUp(signUpForm);
		return new ResponseEntity<>(new LoginResponse("sign up successfully"), HttpStatus.CREATED);
	}

	@GetMapping("/authorize")
	public ResponseEntity<AuthorizeResponse> authorize(@RequestParam(name = "token") String accesstoken) {
		log.info("accesstoken: {}", accesstoken);
		return new ResponseEntity<>(authServerService.authorizeToken(accesstoken), HttpStatus.OK);
	}

}
