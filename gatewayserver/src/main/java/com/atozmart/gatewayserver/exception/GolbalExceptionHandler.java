package com.atozmart.gatewayserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.exception.dto.GlobalErrorResponse;

@RestControllerAdvice
public class GolbalExceptionHandler {

	@ExceptionHandler(GatewayException.class)
	public ResponseEntity<GlobalErrorResponse> handlerGatewayException(GatewayException ex) {

		return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), ex.getHttpStatus());
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<GlobalErrorResponse> handlerUsernameNotFoundException(UsernameNotFoundException ex) {

		return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
	}

}
