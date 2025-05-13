package com.atozmart.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.order.dto.OrderErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OrderException.class)
	public ResponseEntity<OrderErrorResponse> handleCartException(OrderException ce) {
		return new ResponseEntity<>(new OrderErrorResponse(ce.getMessage()), ce.getHttpStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<OrderErrorResponse> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {

		StringBuilder strBuilder = new StringBuilder();
		ex.getBindingResult().getAllErrors().forEach(error -> strBuilder.append(error.getDefaultMessage() + ", "));

		return new ResponseEntity<>(new OrderErrorResponse(strBuilder.toString()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<OrderErrorResponse> handleGeneralException(Exception e) {
		return new ResponseEntity<>(new OrderErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
