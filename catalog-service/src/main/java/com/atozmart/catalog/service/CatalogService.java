package com.atozmart.catalog.service;

import java.util.List;

import com.atozmart.catalog.dto.ItemDto;
import com.atozmart.catalog.exception.CatalogException;

public interface CatalogService {
	
	List<ItemDto> getItems() throws CatalogException;

	List<ItemDto> getItems(int pageNo, int size, String sortBy, String sortDirection) throws CatalogException;

}
