package com.atozmart.catalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.catalog.dto.NewItemDto;
import com.atozmart.catalog.dto.StockUpdateDto;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.service.CatalogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class CatalogAdminController {

	private final CatalogService catalogService;

	@PostMapping("/item")
	public ResponseEntity<Void> addNewItem(@Valid @RequestBody NewItemDto newItemDto) throws CatalogException {
		catalogService.addNewItem(newItemDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping("/item/stock")
	public ResponseEntity<Void> updateStock(@Valid @RequestBody StockUpdateDto stockUpdateDto)
			throws CatalogException {
		log.info("stockUpdateDto: {}",stockUpdateDto);
		catalogService.updateStock(stockUpdateDto);
		return ResponseEntity.ok().build();

	}

}
