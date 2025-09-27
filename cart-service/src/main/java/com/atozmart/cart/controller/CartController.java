package com.atozmart.cart.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping("/items")
	public ResponseEntity<Object> addToCart(@RequestHeader("X-Username") String username,
			@RequestBody @Valid ItemDto item) {
		log.info("X-Username: {}", username);
		cartService.addOrUpdateItemInCart(item, username);
		return ResponseEntity.created(URI.create("/items")).build();
	}

	@GetMapping("/items")
	public ResponseEntity<ViewCartResponse> viewCart(@RequestHeader("X-Username") String username)
			throws CartException {
		log.info("X-Username: {}", username);
		return ResponseEntity.ok(cartService.getCartDetails(username));
	}

	@DeleteMapping("/items")
	public ResponseEntity<Void> removeItemsFromCart(@RequestHeader("X-Username") String username,
			@RequestParam(required = false) String itemId) {
		log.info("X-Username: {}", username);
		cartService.removeItemsFromCart(username, itemId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/items/quantity")
	public ResponseEntity<Void> updateItemQuantity(@RequestHeader("X-Username") String username,
			@RequestBody @Valid ItemDto item) {
		log.info("X-Username: {}", username);
		cartService.updateItemQuantity(item, username);
		return ResponseEntity.ok().build();

	}

	@PostMapping("/checkout/payment")
	public ResponseEntity<CheckOutResponse> proceedToPayment(@RequestHeader("X-Username") String username,
			@RequestHeader(name = "X-User-Email", required = false) String email,
			@RequestBody @Valid CheckOutRequest checkOutRequest) throws CartException {
		log.info("X-Username: {}", username);
		String orderId = cartService.proceedToPayment(username, email, checkOutRequest);
		return new ResponseEntity<>(new CheckOutResponse(orderId, "order placed successfully"), HttpStatus.CREATED);
	}

}
