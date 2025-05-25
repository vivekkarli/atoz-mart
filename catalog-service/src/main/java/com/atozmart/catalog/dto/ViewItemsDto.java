package com.atozmart.catalog.dto;

import java.util.List;

public record ViewItemsDto(

		int noOfItems, int totalPages, List<ItemDto> items

) {
}
