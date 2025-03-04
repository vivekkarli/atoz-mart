package com.atozmart.cart.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.cart.dto.ItemDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CartController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);
	
	@GetMapping("/items")
	public String viewItems() {
		log.debug("accessed cart get endpoint");
		return "accessed cart get endpoint";
	}
	
	@PostMapping("/items")
	public String addItem(@RequestBody ItemDto item) {
		log.debug("accessed cart post endpoint");
		log.info("added items to the cart");
		return "added items to the cart";
	}
	
	@DeleteMapping("/items")
	public String removeItems() {
		log.debug("accessed cart delete endpoint");
		return "accessed cart delete endpoint";
	}

}
