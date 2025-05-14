package com.atozmart.cart.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.dto.CustomErrorResponse;
import com.atozmart.commons.dto.DownStreamException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(DownStreamException.class)
	public ResponseEntity<CustomErrorResponse> handleDownStreamException(DownStreamException ex) {

		try {
			CustomErrorResponse customErrorResponse = new ObjectMapper().readValue(ex.getMessage(),
					CustomErrorResponse.class);
			return new ResponseEntity<>(customErrorResponse, ex.getHttpStatus());
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), null), ex.getHttpStatus());
		}

	}

	@ExceptionHandler(CartException.class)
	public ResponseEntity<CustomErrorResponse> handleCartException(CartException ce) {
		return new ResponseEntity<>(new CustomErrorResponse(ce.getMessage(), null), ce.getHttpStatus());
	}

}
