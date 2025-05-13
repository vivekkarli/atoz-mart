package com.atozmart.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.dto.PlaceOrderResponce;
import com.atozmart.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class OrderController {
	
	private OrderService orderService;

	@PostMapping("/placeOrder")
	public ResponseEntity<PlaceOrderResponce> placeOrder(@RequestHeader("X-Username") String username,
			@RequestBody @Valid PlaceOrderRequest placeOrderRequest) {
		return new ResponseEntity<>(orderService.placeOrder(username, placeOrderRequest), HttpStatus.CREATED);
	}
	
	// TODO view orders 
	
	// TODO update status of orders. by admin and agent
	

}
