package com.atozmart.profile.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ProfileException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public ProfileException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;
	}

}
