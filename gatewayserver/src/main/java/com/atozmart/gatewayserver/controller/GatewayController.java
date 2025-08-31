package com.atozmart.gatewayserver.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.gatewayserver.dto.FallBackResponse;

@RestController
public class GatewayController {

	@GetMapping("/test")
	public String testEndpoint() {
		return "test";
	}

	@RequestMapping("/fallback/authserver")
	public ResponseEntity<FallBackResponse> authserverFallback() {
		return ResponseEntity.status(503).body(new FallBackResponse(
				"authserver not responding, please try again after some time", LocalDateTime.now()));
	}

	@RequestMapping("/fallback/order-service")
	public ResponseEntity<FallBackResponse> orderServiceFallback() {
		return ResponseEntity.status(503).body(new FallBackResponse(
				"order service not responding, please try again after some time", LocalDateTime.now()));
	}

	@RequestMapping("/fallback/catalog-service")
	public ResponseEntity<FallBackResponse> catalogServiceFallback() {
		return ResponseEntity.status(503).body(new FallBackResponse(
				"catalog service not responding, please try again after some time", LocalDateTime.now()));
	}

	@RequestMapping("/fallback/profile-service")
	public ResponseEntity<FallBackResponse> profileServiceFallback() {
		return ResponseEntity.status(503).body(new FallBackResponse(
				"profile service not responding, please try again after some time", LocalDateTime.now()));
	}

	@RequestMapping("/fallback/wishlist-service")
	public ResponseEntity<FallBackResponse> wishlistServiceFallback() {
		return ResponseEntity.status(503).body(new FallBackResponse(
				"wishlist service not responding, please try again after some time", LocalDateTime.now()));
	}

	@RequestMapping("/fallback/cart-service")
	public ResponseEntity<FallBackResponse> cartServiceFallback() {
		return ResponseEntity.status(503).body(new FallBackResponse(
				"cart service not responding, please try again after some time", LocalDateTime.now()));
	}

}
