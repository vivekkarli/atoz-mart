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
	public List<Item> getItems(int pageNo, int size, String sortBy, String sortDirection) {

		Sort sort = Sort.by(Direction.ASC, sortBy);
		
		if (sortDirection.equals("desc"))
			sort = Sort.by(Direction.DESC, sortBy);

		Page<Item> pages = itemRepo.findAll(PageRequest.of(pageNo, size).withSort(sort));
		return pages.getContent();
	}

}
