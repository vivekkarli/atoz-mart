package com.atozmart.cart.dto.order;

import java.math.BigDecimal;

public record OrderItemsDto(

		String itemId,

		String itemName,

		BigDecimal unitPrice,

		int quantity,

		BigDecimal effectivePrice) {

}
