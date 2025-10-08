package com.atozmart.catalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.catalog.entity.ImageData;

public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {

	List<ImageData> findByItemIdIn(List<String> ids);
	
}
