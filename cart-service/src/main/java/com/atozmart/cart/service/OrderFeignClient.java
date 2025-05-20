package com.atozmart.cart.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.atozmart.cart.dto.order.PlaceOrderRequest;
import com.atozmart.cart.dto.order.PlaceOrderResponce;

import jakarta.validation.Valid;

@FeignClient(name = "order-service")
public interface OrderFeignClient {

	@PostMapping("/placeOrder")
	public ResponseEntity<PlaceOrderResponce> placeOrder(@RequestHeader("X-Username") String username,
			@RequestHeader("X-User-Email") String email, @RequestBody @Valid PlaceOrderRequest placeOrderRequest);

}
