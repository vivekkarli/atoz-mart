package com.atozmart.wishlist.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.dto.CustomErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(WishlistException.class)
	public ResponseEntity<CustomErrorResponse> handlerWishlistException(WishlistException ex) {
		return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), null), ex.getHttpStatus());

	}

}
