package com.atozmart.cart.dto.order;

import lombok.Data;

@Data
public class OrderItemsDto {
	private String item;

	private double unitPrice;

	private int quantity;

	private double effectivePrice;

}
