package com.atozmart.catalog.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.catalog.dto.ItemDto;
import com.atozmart.catalog.entity.Item;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.repository.ItemRepository;
import com.atozmart.catalog.service.CatalogService;

import lombok.AllArgsConstructor;

@CrossOrigin
@RestController
@AllArgsConstructor
public class CatalogController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogController.class);

	private CatalogService catalogService;

	@GetMapping(value = "/items/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ItemDto>> viewAllItems(@RequestHeader("atozmart-correlation-id") String correlationId)
			throws CatalogException {
		LOGGER.debug("atozmart-correlation-id: {}", correlationId);
		List<ItemDto> itemDtos = catalogService.getItems();
		return ResponseEntity.ok().body(itemDtos);
	}

	// /items?page-no=0&size=10&sort-by=name
	@GetMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ItemDto>> viewItems(@RequestParam(name = "page", defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(name = "sort-by", defaultValue = "name") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String sortDirection,
			@RequestHeader("atozmart-correlation-id") String correlationId) throws CatalogException {

		LOGGER.debug("atozmart-correlation-id: {}", correlationId);
		List<ItemDto> itemDtos = catalogService.getItems(pageNo, size, sortBy, sortDirection);
		return ResponseEntity.ok().body(itemDtos);
	}
	
	@PostMapping("/items")
	public String postmapping() {
		return "accessed catalog post endpoint";
	}

}
