package com.atozmart.cart.dto;

import jakarta.validation.constraints.Min;

public record ItemDto(

		String itemId,

		String itemName,

		double unitPrice,

		@Min(1) int quantity,

		double effectivePrice) {

}
