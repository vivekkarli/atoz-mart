package com.atozmart.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record NewItemDto(
		
		String id,

		@NotBlank(message = "provide item name")
		String name,

		double unitPrice,
		
		String details,

		@NotBlank(message = "provide category")
		String category,
		
		String categoryDescription,
		
		@Min(value = 1, message = "stock value should not be 0")
		Integer stock) {

}
