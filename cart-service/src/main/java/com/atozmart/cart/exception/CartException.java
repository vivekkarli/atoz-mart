package com.atozmart.cart.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CartException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public CartException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;

	}

}
