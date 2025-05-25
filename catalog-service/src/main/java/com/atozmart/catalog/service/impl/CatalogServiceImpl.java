package com.atozmart.catalog.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.catalog.dao.CatalogDao;
import com.atozmart.catalog.dto.ItemDto;
import com.atozmart.catalog.dto.ViewItemsDto;
import com.atozmart.catalog.entity.Item;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.service.CatalogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CatalogServiceImpl implements CatalogService {

	private ModelMapper mapper;

	private CatalogDao catalogDao;

	@Override
	public ViewItemsDto getItems() throws CatalogException {
		List<Item> items = catalogDao.getItems();

		if (items.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		List<ItemDto> itemsDto = items.stream().map(item -> mapper.map(item, ItemDto.class)).toList();
		return new ViewItemsDto(itemsDto.size(), 1, itemsDto);

	}

	@Override
	public ViewItemsDto getItems(int pageNo, int size, String sortBy, String sortDirection, boolean isLastPage)
			throws CatalogException {
		Page<Item> pageItems = catalogDao.getItems(pageNo, size, sortBy, sortDirection, isLastPage);

		if (pageItems.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		List<ItemDto> itemsDto = pageItems.getContent().stream().map(item -> mapper.map(item, ItemDto.class)).toList();

		return new ViewItemsDto(pageItems.getNumberOfElements(), pageItems.getTotalPages(), itemsDto);
	}

}
