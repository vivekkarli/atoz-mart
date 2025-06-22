package com.atozmart.cart.dto.order;

import java.util.List;

public record PlaceOrderRequest(double orderAmount,

		String couponCode,

		double orderSavings,

		double orderTotal,

		String paymentMode,
		
		List<OrderItemsDto> items

) {

}
