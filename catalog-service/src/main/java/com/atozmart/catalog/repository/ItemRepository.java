package com.atozmart.catalog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.atozmart.catalog.entity.Item;

public interface ItemRepository extends JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {

	@EntityGraph(attributePaths = { "category" })
	List<Item> findAll();

	@EntityGraph(attributePaths = { "category" })
	Page<Item> findAll(Specification<Item> specification, Pageable pageable);

	@EntityGraph(attributePaths = { "category" })
	List<Item> findAll(Specification<Item> spec);

}
