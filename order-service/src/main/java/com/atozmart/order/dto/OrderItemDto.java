package com.atozmart.order.dto;

public record OrderItemDto(

		String itemId,

		String itemName,

		double unitPrice,

		int quantity,

		double effectivePrice) {

}
