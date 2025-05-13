package com.atozmart.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.cart.dto.CartErrorResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CartException.class)
	public ResponseEntity<CartErrorResponse> handleCartException(CartException ce) {
		return new ResponseEntity<>(new CartErrorResponse(ce.getMessage()), ce.getHttpStatus());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<CartErrorResponse> handleValidationExceptions(ConstraintViolationException ex) {

		StringBuilder strBuilder = new StringBuilder();
		ex.getConstraintViolations().forEach(
				voilations -> strBuilder.append("," + voilations.getPropertyPath() + ": " + voilations.getMessage()));

		return new ResponseEntity<>(new CartErrorResponse(strBuilder.toString()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CartErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

		StringBuilder strBuilder = new StringBuilder();
		ex.getBindingResult().getAllErrors().forEach(error -> strBuilder.append(error.getDefaultMessage() + ", "));

		return new ResponseEntity<>(new CartErrorResponse(strBuilder.toString()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<CartErrorResponse> handleGeneralException(Exception e) {
		return new ResponseEntity<>(new CartErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
