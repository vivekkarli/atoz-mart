package com.atozmart.catalog.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.catalog.entity.Inventory;
import com.atozmart.catalog.entity.Item;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
	
	List<Inventory> findAllByItemIn(Set<Item> items);

}
