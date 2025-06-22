package com.atozmart.catalog.dto;

import java.util.List;

import jakarta.validation.Valid;

public record StockUpdateDto(

		@Valid
		List<SingleStockUpdateDto> singleStockUpdates) {

}
