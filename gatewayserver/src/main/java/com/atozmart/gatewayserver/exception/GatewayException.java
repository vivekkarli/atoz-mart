package com.atozmart.gatewayserver.exception;

import org.springframework.http.HttpStatus;

public class GatewayException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final HttpStatus httpStatus;

	public GatewayException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	

}
