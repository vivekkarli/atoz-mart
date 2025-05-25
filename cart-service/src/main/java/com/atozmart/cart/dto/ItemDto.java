package com.atozmart.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ItemDto {
	
	private String itemName;
	
	private double unitPrice;
	
	@Min(1)
	private int quantity;
	
	private double effectivePrice;

}
