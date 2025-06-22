package com.atozmart.order.dto.catalog;

import java.util.List;

import jakarta.validation.Valid;

public record StockUpdateDto(

		@Valid
		List<SingleStockUpdateDto> singleStockUpdates) {

}
