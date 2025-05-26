package com.atozmart.wishlist.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.exception.dto.DownStreamException;
import com.atozmart.commons.exception.dto.GlobalErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class WishlistExceptionHandler {

	@ExceptionHandler(WishlistException.class)
	public ResponseEntity<GlobalErrorResponse> handlerWishlistException(WishlistException ex) {
		return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), ex.getHttpStatus());

	}
	
	@ExceptionHandler(DownStreamException.class)
	public ResponseEntity<GlobalErrorResponse> handleDownStreamException(DownStreamException ex) {

		try {
			GlobalErrorResponse customErrorResponse = new ObjectMapper().readValue(ex.getMessage(),
					GlobalErrorResponse.class);
			return new ResponseEntity<>(customErrorResponse, ex.getHttpStatus());
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), ex.getHttpStatus());
		}

	}

}
