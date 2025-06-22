package com.atozmart.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record SingleStockUpdateDto(@NotEmpty(message = "provide itemId") String itemId,

		@Min(value = 1, message = "incrementBy value should not be 0") Integer incrementBy,

		@Min(value = 1, message = "decrementBy value should not be 0") Integer decrementBy) {

}
