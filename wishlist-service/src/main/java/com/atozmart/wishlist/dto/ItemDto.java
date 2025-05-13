package com.atozmart.wishlist.dto;

import lombok.Data;

@Data
public class ItemDto {
	
	private String item;
	
	private double unitPrice;
	
	private int quantity;
	
	private double effectivePrice;

}
