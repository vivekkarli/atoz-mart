package com.atozmart.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record ViewCartResponse(

		List<ItemDto> items,

		BigDecimal orderAmount) {

}
