package com.atozmart.authserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.exception.dto.GlobalErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<GlobalErrorResponse> handlerBadCredentialsException(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GlobalErrorResponse(ex.getMessage(), null));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<GlobalErrorResponse> handlerUsernameNotFoundException(UsernameNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GlobalErrorResponse(ex.getMessage(), null));
	}

	@ExceptionHandler(AuthServerException.class)
	public ResponseEntity<GlobalErrorResponse> handlerAuthServerException(AuthServerException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GlobalErrorResponse(ex.getMessage(), null));
	}

}
