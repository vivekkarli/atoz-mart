package com.atozmart.cart.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Pattern;

public record CheckOutRequest(

		BigDecimal orderAmount,

		String couponCode,

		BigDecimal orderSavings,

		BigDecimal orderTotal,

		@Pattern(regexp = "COD|UPI", message = "paymentMode should be COD, UPI only") 
		String paymentMode

) {

}
