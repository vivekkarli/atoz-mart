package com.atozmart.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.profile.entity.ProfilePhotoMetadata;

public interface ProfilePhotoMetadataRepo extends JpaRepository<ProfilePhotoMetadata, Integer> {

	Optional<ProfilePhotoMetadata> findByUserProfileUsername(String username);
}
