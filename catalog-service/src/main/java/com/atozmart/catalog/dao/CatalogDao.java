package com.atozmart.catalog.dao;

import java.util.List;

import com.atozmart.catalog.entity.Item;

public interface CatalogDao {

	List<Item> getItems();

	List<Item> getItems(int pageNo, int size, String sortBy, String sortDirection);

}
