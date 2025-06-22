package com.atozmart.order.dto;

import java.util.List;

public record PlaceOrderRequest(double orderAmount,

		String couponCode,

		double orderSavings,

		double orderTotal,

		String paymentMode,
		
		List<OrderItemDto> items

) {

}
