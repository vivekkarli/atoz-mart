package com.atozmart.cart.dto.order;

public record OrderItemsDto(

		String itemId,

		String itemName,

		double unitPrice,

		int quantity,

		double effectivePrice) {

}
