package com.atozmart.order.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.exception.dto.DownStreamException;
import com.atozmart.commons.exception.dto.GlobalErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OrderException.class)
	public ResponseEntity<GlobalErrorResponse> handleCartException(OrderException ce) {
		return new ResponseEntity<>(new GlobalErrorResponse(ce.getMessage(), null), ce.getHttpStatus());
	}
	
	@ExceptionHandler(DownStreamException.class)
	public ResponseEntity<GlobalErrorResponse> handleDownStreamException(DownStreamException ex) {

		try {
			GlobalErrorResponse globalErrorResponse = new ObjectMapper().readValue(ex.getMessage(),
					GlobalErrorResponse.class);
			return new ResponseEntity<>(globalErrorResponse, ex.getHttpStatus());
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), ex.getHttpStatus());
		}

	}

}
