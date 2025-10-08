package com.atozmart.catalog.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.atozmart.catalog.dto.ImageDataDto;
import com.atozmart.catalog.dto.NewItemDto;
import com.atozmart.catalog.dto.PageDto;
import com.atozmart.catalog.dto.SearchFilters;
import com.atozmart.catalog.dto.SingleStockUpdateDto;
import com.atozmart.catalog.entity.Category;
import com.atozmart.catalog.entity.ImageData;
import com.atozmart.catalog.entity.Inventory;
import com.atozmart.catalog.entity.Item;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.repository.CategoryRepository;
import com.atozmart.catalog.repository.ImageDataRepository;
import com.atozmart.catalog.repository.InventoryRepository;
import com.atozmart.catalog.repository.ItemRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class CatalogDao {

	private final ItemRepository itemRepo;

	private final CategoryRepository categoryRepo;

	private final InventoryRepository inventoryRepo;

	private final ImageDataRepository imageDataRepo;

	public List<Item> getAllItems() {
		return itemRepo.findAll();
	}

	public List<Category> getAllCategories() {
		return categoryRepo.findAll();
	}

	public Page<Item> getItems(SearchFilters searchFilters, PageDto pageDto) {

		return itemRepo.findAll(CatalogDaoHelper.withSearchCriteria(searchFilters),
				CatalogDaoHelper.getPageableObj(pageDto, itemRepo));

	}

	@Transactional
	public void addNewItem(NewItemDto newItemDto) throws CatalogException {
		// check if item already exists
		if (itemRepo.existsById(newItemDto.id()))
			throw new CatalogException("item already exists", HttpStatus.BAD_REQUEST);

		Category category = new Category();
		category.setName(newItemDto.category());
		category.setDescription(newItemDto.categoryDescription());
		// check if catagory exists
		if (!categoryRepo.existsById(newItemDto.category())) {
			// if not exists add new category
			categoryRepo.save(category);
		}

		// add new Item
		Item item = new Item();
		item.setId(newItemDto.id());
		item.setName(newItemDto.name());
		item.setUnitPrice(newItemDto.unitPrice());
		item.setDetails(newItemDto.details());
		item.setCategory(category);

		Inventory inventory = new Inventory();
		inventory.setCategory(newItemDto.category());
		inventory.setStock(newItemDto.stock());

		item.setInventory(inventory);
		inventory.setItem(item);
		itemRepo.save(item);

	}

	@Transactional
	public void updateStock(List<SingleStockUpdateDto> singleStockUpdates) throws CatalogException {

		Set<Item> items = new HashSet<>();
		singleStockUpdates.forEach(singleStockUpdate -> {
			Item item = new Item();
			item.setId(singleStockUpdate.itemId());
			items.add(item);
		});

		List<Inventory> inventories = inventoryRepo.findAllByItemIn(items);

		for (Inventory inventory : inventories) {
			Optional<SingleStockUpdateDto> ssuOptional = singleStockUpdates.stream()
					.filter(s -> s.itemId().equals(inventory.getItem().getId())).findFirst();
			updateStock(inventory, ssuOptional.get());

		}

	}

	private void updateStock(Inventory inventory, SingleStockUpdateDto singleStockUpdateDto) throws CatalogException {

		Integer availableStock = inventory.getStock();

		if (singleStockUpdateDto.incrementBy() != null) {
			inventory.setStock(availableStock + singleStockUpdateDto.incrementBy());
		}

		if (singleStockUpdateDto.decrementBy() != null) {

			if (availableStock == 0) {
				throw new CatalogException("item is already out of stock", HttpStatus.NOT_ACCEPTABLE);
			}

			if (availableStock - singleStockUpdateDto.decrementBy() < 0) {
				throw new CatalogException(
						"can't decrement more than the available stock: %d".formatted(availableStock),
						HttpStatus.NOT_ACCEPTABLE);
			}

			inventory.setStock(inventory.getStock() - singleStockUpdateDto.decrementBy());
		}

	}

	public List<ImageDataDto> findImageData(List<String> ids) {
		List<ImageData> imageDataLst = imageDataRepo.findByItemIdIn(ids);

		return imageDataLst.stream()
				.map(imageData -> new ImageDataDto(imageData.getItem().getId(), imageData.getLocation())).toList();

	}
}