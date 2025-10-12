package com.atozmart.catalog.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atozmart.catalog.dto.ImageMetadataDto;
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

	@GetMapping("/image/{item-id}")
	public ResponseEntity<Resource> getImages(@PathVariable(name = "item-id") String itemId) throws CatalogException {
		return ResponseEntity.ok(catalogService.getImage(itemId));
	}
	
	@GetMapping("/image")
	public ResponseEntity<List<ImageMetadataDto>> getImageMetadata(@RequestParam(name = "item-id") List<String> itemIds) throws CatalogException {
		return ResponseEntity.ok(catalogService.getImageMetadata(itemIds));
	}

	@PostMapping("/image/{item-id}")
	public ResponseEntity<Void> uploadImage(@PathVariable(name = "item-id") String itemId,
			@RequestParam("file") MultipartFile file) throws CatalogException {
		URI uri = catalogService.uploadImage(itemId, file);
		return ResponseEntity.created(uri).build();

	}

}
