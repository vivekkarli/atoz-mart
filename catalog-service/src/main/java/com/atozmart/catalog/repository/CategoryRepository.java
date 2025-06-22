package com.atozmart.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.catalog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String>{
	
	

}
