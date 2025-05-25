package com.atozmart.catalog.service;

import com.atozmart.catalog.dto.ViewItemsDto;
import com.atozmart.catalog.exception.CatalogException;

public interface CatalogService {
	
	ViewItemsDto getItems() throws CatalogException;

	ViewItemsDto getItems(int pageNo, int size, String sortBy, String sortDirection, boolean isLastPage) throws CatalogException;

}
