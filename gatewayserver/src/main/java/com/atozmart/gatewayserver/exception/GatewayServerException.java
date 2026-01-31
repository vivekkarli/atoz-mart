package com.atozmart.gatewayserver.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class GatewayServerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public GatewayServerException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
