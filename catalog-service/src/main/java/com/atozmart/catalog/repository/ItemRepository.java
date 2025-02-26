package com.atozmart.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.catalog.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer>{

}
