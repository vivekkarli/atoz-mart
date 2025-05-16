package com.atozmart.commons.exception.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class DownStreamException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public DownStreamException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;

	}
}
