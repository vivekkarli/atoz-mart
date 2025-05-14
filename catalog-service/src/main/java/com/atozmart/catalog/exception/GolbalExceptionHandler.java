package com.atozmart.catalog.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.dto.CustomErrorResponse;

@RestControllerAdvice
public class GolbalExceptionHandler {

	@ExceptionHandler(exception = CatalogException.class)
	public ResponseEntity<CustomErrorResponse> handleCatalogException(CatalogException ce) {
		return new ResponseEntity<>(new CustomErrorResponse(ce.getMessage(), null), ce.getHttpStatus());
	}

}
