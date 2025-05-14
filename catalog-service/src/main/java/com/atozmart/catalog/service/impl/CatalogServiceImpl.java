package com.atozmart.catalog.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.catalog.dao.CatalogDao;
import com.atozmart.catalog.dto.ItemDto;
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
	public List<ItemDto> getItems() throws CatalogException {
		List<Item> items = catalogDao.getItems();

		if (items.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		return items.stream().map(item -> mapper.map(item, ItemDto.class)).toList();

	}

	@Override
	public List<ItemDto> getItems(int pageNo, int size, String sortBy, String sortDirection) throws CatalogException {
		List<Item> items = catalogDao.getItems(pageNo, size, sortBy, sortDirection);

		if (items.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		return items.stream().map(item -> mapper.map(item, ItemDto.class)).toList();
	}

}
