package com.atozmart.cart.dto;

import java.util.List;

public record ViewCartResponse(

		List<ItemDto> items,

		double orderAmount) {

}
