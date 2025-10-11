package com.atozmart.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.catalog.entity.ImageMetadata;

public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Integer> {

	List<ImageMetadata> findByItemIdIn(List<String> ids);
	
	Optional<ImageMetadata> findByItemId(String id);
	
}
