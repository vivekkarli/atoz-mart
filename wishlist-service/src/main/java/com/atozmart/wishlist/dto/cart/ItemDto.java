package com.atozmart.wishlist.dto.cart;

import lombok.Data;

@Data
public class ItemDto {
	
	private String itemName;
	
	private double unitPrice;
	
	private int quantity;
	
	private double effectivePrice;

}
