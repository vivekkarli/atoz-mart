package com.atozmart.catalog.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CatalogException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public CatalogException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
