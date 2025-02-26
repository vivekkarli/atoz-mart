package com.atozmart.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GolbalExceptionHandler {

	@ExceptionHandler(exception = CatalogException.class)
	public ResponseEntity<ErrorDto> handleCatalogException(CatalogException ce) {

		return ResponseEntity.ok().body(new ErrorDto(HttpStatus.OK.value(), ce.getMessage()));

	}

}
