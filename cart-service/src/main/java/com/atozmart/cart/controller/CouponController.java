package com.atozmart.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.cart.dto.CouponResponse;
import com.atozmart.cart.service.CouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {
	
	private final CouponService couponService;
	
	@GetMapping
	public ResponseEntity<List<CouponResponse>> getCoupons() {
		log.debug("accessed getCoupons endpoint");
		return ResponseEntity.ok(couponService.getAvailableCoupons());
	}

}
