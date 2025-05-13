package com.atozmart.cart.dto;

import java.util.List;

import lombok.Data;

@Data
public class ViewCartResponse {

	private List<ItemDto> items;
	
	private double orderAmount;
	
}
