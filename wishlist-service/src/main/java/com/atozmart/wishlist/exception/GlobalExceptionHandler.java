package com.atozmart.wishlist.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atozmart.wishlist.dto.ErrorInfo;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(WishlistException.class)
	public ResponseEntity<ErrorInfo> handlerWishlistException(WishlistException ex) {
		ErrorInfo errorInfo = new ErrorInfo();
		errorInfo.setMsg(ex.getMessage());
		return ResponseEntity.ok(errorInfo);

	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> handlerGeneralException(Exception ex) {

		ErrorInfo errorInfo = new ErrorInfo();
		errorInfo.setMsg(ex.getMessage());
		return ResponseEntity.ok(errorInfo);

	}

}
