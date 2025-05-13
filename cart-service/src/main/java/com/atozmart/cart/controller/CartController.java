package com.atozmart.cart.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.cart.dto.CheckOutRequest;
import com.atozmart.cart.dto.CheckOutResponse;
import com.atozmart.cart.dto.ItemDto;
import com.atozmart.cart.dto.ViewCartResponse;
import com.atozmart.cart.exception.CartException;
import com.atozmart.cart.service.CartService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
public class CartController {

	private CartService cartService;

	@PostMapping("/items")
	public ResponseEntity<Object> addToCart(@RequestHeader("X-Username") String username, @RequestBody ItemDto item) {
		log.info("X-Username: {}", username);
		cartService.addItemsToCart(item, username);
		return ResponseEntity.created(URI.create("/items")).build();
	}

	@GetMapping("/items")
	public ResponseEntity<ViewCartResponse> viewCart(@RequestHeader("X-Username") String username)
			throws CartException {
		log.info("X-Username: {}", username);
		return ResponseEntity.ok(cartService.getCartDetails(username));
	}

	@DeleteMapping("/items")
	public ResponseEntity<Object> removeItemsFromCart(@RequestHeader("X-Username") String username,
			@RequestParam String item, @RequestParam(required = false, defaultValue = "0") @Min(0) int quantity) {
		log.info("X-Username: {}", username);
		cartService.removeItemsFromCart(username, item, quantity);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/checkout")
	public ResponseEntity<CheckOutResponse> proceedToPayment(@RequestHeader("X-Username") String username,
			@RequestBody @Valid CheckOutRequest checkOutRequest) throws CartException{
		String orderId = cartService.proceedToPayment(username, checkOutRequest);
		return new ResponseEntity<>(new CheckOutResponse(orderId, "order placed successfully"), HttpStatus.CREATED);
	}

}
