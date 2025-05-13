package com.atozmart.cart.dto;

import jakarta.validation.constraints.Pattern;

public record CheckOutRequest(

		double orderAmount,

		String couponCode,

		double orderSavings,

		double orderTotal,

		@Pattern(regexp = "COD|UPI", message = "paymentMode should be COD, UPI only") 
		String paymentMode

) {

}
