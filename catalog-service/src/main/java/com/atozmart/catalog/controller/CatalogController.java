package com.atozmart.catalog.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.catalog.dto.ViewItemsDto;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.service.CatalogService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CatalogController {

	private CatalogService catalogService;

	@GetMapping(value = "/items/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ViewItemsDto> viewAllItems()
			throws CatalogException {
		return ResponseEntity.ok().body(catalogService.getItems());
	}

	@GetMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ViewItemsDto> viewItems(@RequestParam(name = "page", defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(name = "sort-by", defaultValue = "name") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String sortDirection,
			@RequestParam(name = "lastPage", defaultValue = "false") boolean isLastPage) throws CatalogException {

		return ResponseEntity.ok().body(catalogService.getItems(pageNo, size, sortBy, sortDirection, isLastPage));
	}
	
	@PostMapping("/items")
	public String postmapping() {
		return "accessed catalog post endpoint";
	}

}
