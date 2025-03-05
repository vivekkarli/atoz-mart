package com.atozmart.gatewayserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GolbalExceptionHandler {

	@ExceptionHandler(GatewayException.class)
	public ResponseEntity<ErrorDto> handlerGatewayException(GatewayException ex) {

		ErrorDto errorDto = new ErrorDto();
		errorDto.setMessage(ex.getMessage());

		return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorDto> handlerUsernameNotFoundException(UsernameNotFoundException ex) {

		ErrorDto errorDto = new ErrorDto();
		errorDto.setMessage(ex.getMessage());

		return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
	}

}
