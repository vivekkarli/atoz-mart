package com.atozmart.commons.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.atozmart.commons.exception.dto.GlobalErrorResponse;

import jakarta.validation.ConstraintViolationException;

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(ConstraintViolationException ex) {

		StringBuilder strBuilder = new StringBuilder();
		ex.getConstraintViolations().forEach(
				voilations -> strBuilder.append("," + voilations.getPropertyPath() + ": " + voilations.getMessage()));

		return new ResponseEntity<>(new GlobalErrorResponse(strBuilder.toString(), null), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, HandlerMethodValidationException.class })
	public ResponseEntity<GlobalErrorResponse> handleMethodArgumentNotValidException(Exception ex) {

		StringBuilder strBuilder = new StringBuilder();

		if (ex instanceof MethodArgumentNotValidException manvEx) {
			manvEx.getBindingResult().getAllErrors()
					.forEach(error -> strBuilder.append(error.getDefaultMessage() + ", "));
		}

		if (ex instanceof HandlerMethodValidationException hmvEx) {
			hmvEx.getAllErrors().forEach(error -> strBuilder.append(error.getDefaultMessage() + ", "));
		}

		return new ResponseEntity<>(new GlobalErrorResponse(strBuilder.toString(), null), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalErrorResponse> handleCartException(Exception ex) {
		return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
