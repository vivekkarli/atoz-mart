package com.atozmart.catalog.dao.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.atozmart.catalog.dao.CatalogDao;
import com.atozmart.catalog.entity.Item;
import com.atozmart.catalog.repository.ItemRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class CatalogDaoImpl implements CatalogDao {

	private ItemRepository itemRepo;

	public List<Item> getItems() {
		return itemRepo.findAll();
	}

	@Override
	public Page<Item> getItems(int pageNo, int size, String sortBy, String sortDirection, boolean isLastPage) {

		Sort sort = Sort.by(Direction.ASC, sortBy);

		if (sortDirection.equals("desc"))
			sort = Sort.by(Direction.DESC, sortBy);

		if (!isLastPage)
			return itemRepo.findAll(PageRequest.of(pageNo, size).withSort(sort));

		long totalItems = itemRepo.count();
		int lastPageNo = (int) (totalItems / size);
		if (totalItems % size == 0 && lastPageNo > 0) {
			lastPageNo--; // Adjust for the case when totalElements is an exact multiple of pageSize
		}
		return itemRepo.findAll(PageRequest.of(lastPageNo, size).withSort(sort));

	}

}
