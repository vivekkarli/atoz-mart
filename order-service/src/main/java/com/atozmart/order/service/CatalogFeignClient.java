package com.atozmart.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atozmart.order.dto.catalog.StockUpdateDto;

import jakarta.validation.Valid;

@FeignClient(name = "catalog-service")
public interface CatalogFeignClient {

	@PutMapping("/admin/item/stock")
	public ResponseEntity<Void> updateStock(@Valid @RequestBody StockUpdateDto stockUpdateDto);
	
}
