package com.atozmart.catalog.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.catalog.dto.ImageDataDto;
import com.atozmart.catalog.dto.PageDto;
import com.atozmart.catalog.dto.SearchFilters;
import com.atozmart.catalog.dto.ViewItemsDto;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.service.CatalogService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CatalogController {

	private CatalogService catalogService;

	@GetMapping(value = "/items/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ViewItemsDto> viewAllItems() throws CatalogException {
		return ResponseEntity.ok().body(catalogService.getItems());
	}

	@GetMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ViewItemsDto> viewItems(@RequestParam(required = false) String category,
			@RequestParam(required = false) Double fromPriceRange, @RequestParam(required = false) Double toPriceRange,
			@RequestParam(required = false) String name, @RequestParam(name = "page", defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(name = "sort-by", defaultValue = "name") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String sortDirection,
			@RequestParam(name = "lastPage", defaultValue = "false") boolean isLastPage) throws CatalogException {

		SearchFilters searchFilters = new SearchFilters(category, fromPriceRange, toPriceRange, name);
		PageDto pageDto = new PageDto(pageNo, size, sortBy, sortDirection, isLastPage);

		return ResponseEntity.ok().body(catalogService.getItems(searchFilters, pageDto));
	}

	@GetMapping("/categories")
	public ResponseEntity<Set<String>> getAllCategories() {
		return ResponseEntity.ok().body(catalogService.getAllCategories());
	}

	@GetMapping("/image")
	public ResponseEntity<List<ImageDataDto>> getImages(@RequestParam(name = "item-id") List<String> itemIds) {
		
		List<ImageDataDto> imageDataDtos = catalogService.getImages(itemIds);
		if(imageDataDtos.size() != itemIds.size())
			return new ResponseEntity<>(imageDataDtos, HttpStatus.PARTIAL_CONTENT);
		
		return ResponseEntity.ok(imageDataDtos);
	}

}
