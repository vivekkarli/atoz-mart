package com.atozmart.cart.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record PlaceOrderRequest(BigDecimal orderAmount,

		String couponCode,

		BigDecimal orderSavings,

		BigDecimal orderTotal,

		String paymentMode,
		
		List<OrderItemsDto> items

) {

}
