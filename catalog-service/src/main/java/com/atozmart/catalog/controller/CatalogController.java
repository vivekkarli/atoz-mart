package com.atozmart.catalog.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.catalog.dto.ItemDto;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.service.CatalogService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CatalogController {

	private CatalogService catalogService;

	@GetMapping(value = "/items/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ItemDto>> viewAllItems()
			throws CatalogException {
		List<ItemDto> itemDtos = catalogService.getItems();
		return ResponseEntity.ok().body(itemDtos);
	}

	// /items?page-no=0&size=10&sort-by=name
	@GetMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ItemDto>> viewItems(@RequestParam(name = "page", defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(name = "sort-by", defaultValue = "name") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String sortDirection) throws CatalogException {

		List<ItemDto> itemDtos = catalogService.getItems(pageNo, size, sortBy, sortDirection);
		return ResponseEntity.ok().body(itemDtos);
	}
	
	@PostMapping("/items")
	public String postmapping() {
		return "accessed catalog post endpoint";
	}

}
