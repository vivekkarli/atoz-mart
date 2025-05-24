package com.atozmart.cart.dto;

import lombok.Data;

@Data
public class ItemDto {
	
	private String itemName;
	
	private double unitPrice;
	
	private int quantity;
	
	private double effectivePrice;

}
