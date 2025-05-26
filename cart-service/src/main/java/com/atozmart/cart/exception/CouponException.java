package com.atozmart.cart.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CouponException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public CouponException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;

	}

}
