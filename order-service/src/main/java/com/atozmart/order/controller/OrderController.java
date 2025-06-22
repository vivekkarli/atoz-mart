package com.atozmart.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.dto.PlaceOrderResponse;
import com.atozmart.order.dto.ViewOrdersDto;
import com.atozmart.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class OrderController {

	private OrderService orderService;

	@PostMapping("/placeOrder")
	public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestHeader("X-Username") String username,
			@RequestHeader(name = "X-User-Email", required = false) String email,
			@RequestBody @Valid PlaceOrderRequest placeOrderRequest) {
		return new ResponseEntity<>(orderService.placeOrder(username, email, placeOrderRequest), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<ViewOrdersDto>> getOrderDetails(@RequestHeader("X-Username") String username,
			@RequestParam(required = false) Integer orderId) {
		
		List<ViewOrdersDto> orderDetails = orderService.getOrderDetails(username, orderId);
		return ResponseEntity.ok(orderDetails);
	}
	
	@PatchMapping("/cancelOrder")
	public ResponseEntity<String> cancelOrder(@RequestHeader("X-Username") String username,
			@RequestParam Integer orderId){
		orderService.cancelOrder(username, orderId);
		return ResponseEntity.ok("order with order id: %s cancelled successfully".formatted(orderId));
	}

	// TODO update status of orders. by admin and agent

}
