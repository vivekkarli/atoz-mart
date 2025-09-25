package com.atozmart.authserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.authserver.dto.ChangePasswordRequest;
import com.atozmart.authserver.dto.ChangePasswordResponse;
import com.atozmart.authserver.dto.ForgotPasswordRequest;
import com.atozmart.authserver.dto.ForgotPasswordResponse;
import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.LoginResponse;
import com.atozmart.authserver.dto.ResetPasswordRequest;
import com.atozmart.authserver.dto.ResetPasswordResponse;
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
	public ResponseEntity<String> confirmEmail(@RequestParam String token) {
		return notificationService.confirmEmail(token);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		notificationService.handleForgotPassword(request);
		return ResponseEntity.ok(new ForgotPasswordResponse("password reset link sent to your mail"));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestParam(name = "token") String encodedToken,
			@Valid @RequestBody ResetPasswordRequest request) {
		notificationService.resetPassword(encodedToken, request);
		return new ResponseEntity<>(new ResetPasswordResponse("password reset successfull"), HttpStatus.ACCEPTED);

	}

	@PostMapping("/change-password")
	public ResponseEntity<ChangePasswordResponse> changePassword(@RequestHeader("X-Username") String username,
			@Valid @RequestBody ChangePasswordRequest request) {
		authServerService.changePassword(username, request);
		return new ResponseEntity<>(new ChangePasswordResponse("password updated successfully"), HttpStatus.ACCEPTED);
	}

}
