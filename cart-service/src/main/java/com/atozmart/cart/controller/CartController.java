package com.atozmart.cart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {
	
	@GetMapping("/items")
	public String getEndPoint() {
		return "accessed cart get endpoint";
	}
	
	@PostMapping("/items")
	public String postEndPoint() {
		return "accessed cart post endpoint";
	}

}
