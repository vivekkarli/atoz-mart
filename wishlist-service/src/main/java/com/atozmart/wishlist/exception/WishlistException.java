package com.atozmart.wishlist.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class WishlistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public WishlistException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
