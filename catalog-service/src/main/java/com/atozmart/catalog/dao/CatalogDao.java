package com.atozmart.catalog.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.atozmart.catalog.entity.Item;

public interface CatalogDao {

	List<Item> getItems();

	Page<Item> getItems(int pageNo, int size, String sortBy, String sortDirection, boolean isLastPage);

}
