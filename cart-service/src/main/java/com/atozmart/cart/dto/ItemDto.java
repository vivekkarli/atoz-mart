package com.atozmart.cart.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

public record ItemDto(

		String itemId,

		String itemName,

		BigDecimal unitPrice,

		@Min(1) int quantity,

		BigDecimal effectivePrice) {

}
