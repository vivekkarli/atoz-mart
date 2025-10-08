package com.atozmart.catalog.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.catalog.dao.CatalogDao;
import com.atozmart.catalog.dto.ImageDataDto;
import com.atozmart.catalog.dto.ItemDto;
import com.atozmart.catalog.dto.NewItemDto;
import com.atozmart.catalog.dto.PageDto;
import com.atozmart.catalog.dto.SearchFilters;
import com.atozmart.catalog.dto.SingleStockUpdateDto;
import com.atozmart.catalog.dto.StockUpdateDto;
import com.atozmart.catalog.dto.ViewItemsDto;
import com.atozmart.catalog.entity.Category;
import com.atozmart.catalog.entity.Item;
import com.atozmart.catalog.exception.CatalogException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CatalogService {

	private final CatalogDao catalogDao;

	public ViewItemsDto getItems() throws CatalogException {
		List<Item> items = catalogDao.getAllItems();

		if (items.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		List<ItemDto> itemsDto = items.stream().map(item -> new ItemDto(item.getId(), item.getName(),
				item.getUnitPrice(), item.getDetails(), item.getCategory().getName())).toList();
		return new ViewItemsDto(itemsDto.size(), 1, itemsDto);

	}

	public ViewItemsDto getItems(SearchFilters searchFilters, PageDto pageDto) throws CatalogException {

		Page<Item> pageItems = catalogDao.getItems(searchFilters, pageDto);

		if (pageItems.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		List<ItemDto> itemsDto = pageItems.getContent().stream().map(item -> new ItemDto(item.getId(), item.getName(),
				item.getUnitPrice(), item.getDetails(), item.getCategory().getName())).toList();

		return new ViewItemsDto(pageItems.getNumberOfElements(), pageItems.getTotalPages(), itemsDto);

	}

	public Set<String> getAllCategories() {
		List<Category> allCategories = catalogDao.getAllCategories();
		return allCategories.stream().map(Category::getName).collect(Collectors.toSet());
	}

	public void addNewItem(NewItemDto newItemDto) throws CatalogException {
		catalogDao.addNewItem(newItemDto);
	}

	public void updateStock(StockUpdateDto stockUpdateDto) throws CatalogException {

		if (stockUpdateDto.singleStockUpdates().isEmpty())
			throw new CatalogException("provide stock details", HttpStatus.BAD_REQUEST);

		for (SingleStockUpdateDto singleStockUpdate : stockUpdateDto.singleStockUpdates()) {
			if (singleStockUpdate.incrementBy() == null && singleStockUpdate.decrementBy() == null)
				throw new CatalogException(
						"itemId: %s, provide any one update criteria, either incrementBy or decrementBy"
								.formatted(singleStockUpdate.itemId()),
						HttpStatus.BAD_REQUEST);

			if (singleStockUpdate.incrementBy() != null && singleStockUpdate.decrementBy() != null)
				throw new CatalogException(
						"itemId: %s, provide only one update criteria, either incrementBy or decrementBy"
								.formatted(singleStockUpdate.itemId()),
						HttpStatus.BAD_REQUEST);
		}

		catalogDao.updateStock(stockUpdateDto.singleStockUpdates());

	}

	public List<ImageDataDto> getImages(List<String> ids) {
		return catalogDao.findImageData(ids);
	}

}
