package com.atozmart.cart.dto.order;

import java.util.List;

import lombok.Data;

@Data
public class PlaceOrderRequest {
	private double orderAmount;

	private String couponCode;

	private double orderSavings;

	private double orderTotal;

	private String paymentMode;

	private List<OrderItemsDto> items;

}
