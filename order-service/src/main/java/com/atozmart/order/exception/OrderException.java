package com.atozmart.order.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String msg;

	private HttpStatus httpStatus;

	public OrderException(final String msg, final HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;
	}

}
