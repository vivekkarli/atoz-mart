package com.atozmart.catalog.dto;

public record ItemDto(
		
		String id,

		String name,

		double unitPrice,
		
		String details,

		String category,
		
		String imageUrl) {

}
