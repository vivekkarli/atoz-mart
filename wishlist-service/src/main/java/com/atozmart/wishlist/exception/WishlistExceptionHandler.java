package com.atozmart.wishlist.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.commons.exception.dto.GlobalErrorResponse;

@RestControllerAdvice
public class WishlistExceptionHandler {

	@ExceptionHandler(WishlistException.class)
	public ResponseEntity<GlobalErrorResponse> handlerWishlistException(WishlistException ex) {
		return new ResponseEntity<>(new GlobalErrorResponse(ex.getMessage(), null), ex.getHttpStatus());

	}

}
