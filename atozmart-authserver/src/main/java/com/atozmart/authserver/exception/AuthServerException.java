package com.atozmart.authserver.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AuthServerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public AuthServerException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
