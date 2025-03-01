package com.atozmart.cart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);
	
	@GetMapping("/items")
	public String getEndPoint() {
		LOGGER.debug("accessed cart get endpoint");
		return "accessed cart get endpoint";
	}
	
	@PostMapping("/items")
	public String postEndPoint() {
		LOGGER.debug("accessed cart post endpoint");
		return "accessed cart post endpoint";
	}
	
	@DeleteMapping("/items")
	public String deleteEndPoint() {
		LOGGER.debug("accessed cart delete endpoint");
		return "accessed cart delete endpoint";
	}

}
