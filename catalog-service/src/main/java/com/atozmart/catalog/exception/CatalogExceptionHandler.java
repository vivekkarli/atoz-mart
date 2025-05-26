package com.atozmart.catalog.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.exception.dto.GlobalErrorResponse;

@RestControllerAdvice
public class CatalogExceptionHandler {

	@ExceptionHandler(exception = CatalogException.class)
	public ResponseEntity<GlobalErrorResponse> handleCatalogException(CatalogException ex) {
		return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), ex.getHttpStatus());
	}

}
