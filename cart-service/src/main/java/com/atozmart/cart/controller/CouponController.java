package com.atozmart.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.cart.dto.ViewCartResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponController {
	
	@GetMapping
	public ResponseEntity<ViewCartResponse> getCoupons() {
		log.debug("accessed getCoupons endpoint");
		// TODO views coupons and discounts
		// Front end will calculate the discount with the help of this
		return ResponseEntity.ok(null);
	}

}
