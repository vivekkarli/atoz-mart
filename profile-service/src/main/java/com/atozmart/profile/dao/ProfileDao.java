package com.atozmart.profile.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.atozmart.profile.dto.ProfilePhotoMetadataDto;
import com.atozmart.profile.entity.ProfilePhotoMetadata;
import com.atozmart.profile.entity.UserAddress;
import com.atozmart.profile.entity.UserAddressCompKey;
import com.atozmart.profile.entity.UserProfile;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.repository.ProfilePhotoMetadataRepo;
import com.atozmart.profile.repository.UserAddressRepository;
import com.atozmart.profile.repository.UserProfileRepository;
import com.atozmart.profile.util.AddressTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProfileDao {

	private final UserProfileRepository userProfileRepo;

	private final UserAddressRepository userAddressRepo;

	private final ProfilePhotoMetadataRepo profilePhotoMetadataRepo;

	public UserProfile getProfileDetails(String username) {

		return userProfileRepo.findById(username)
				.orElseThrow(() -> new ProfileException("no profile found", HttpStatus.NOT_FOUND));

	}

	@Transactional
	public void addOrUpdateProfile(UserProfile userProfile) {

		var addressesTemp = new HashSet<>(userProfile.getAddresses());

		userProfile.setAddresses(Collections.emptySet());
		userProfileRepo.save(userProfile);

		Optional.ofNullable(addressesTemp).ifPresent(userAddressRepo::saveAll);

	}

	public void deleteProfile(String username) {
		userProfileRepo.deleteById(username);
	}

	public boolean doesProfileExists(String username) {
		return userProfileRepo.existsById(username);
	}

	@Transactional
	public void deleteAddress(String username, String addressType) {

		Optional.ofNullable(addressType).ifPresentOrElse(
				type -> userAddressRepo.deleteById(new UserAddressCompKey(username, AddressTypeEnum.fromString(type))),
				() -> userAddressRepo.deleteByUsername(username));

	}

	@Transactional
	public void changeDefaultTo(String username, String addressType) {

		UserAddress existingAddress = userAddressRepo
				.findById(new UserAddressCompKey(username, AddressTypeEnum.fromString(addressType)))
				.orElseThrow(() -> new ProfileException("address not found with address type %s".formatted(addressType),
						HttpStatus.NOT_FOUND));

		userAddressRepo.findByUsernameAndDefaultAddress(username, true).ifPresentOrElse(
				existingDefaultAddress -> existingDefaultAddress.setDefaultAddress(false),
				() -> log.info("existing default address not found"));

		existingAddress.setDefaultAddress(true);

	}

	public void saveProfilePhotoMetadata(ProfilePhotoMetadataDto dto) {

		profilePhotoMetadataRepo.findByUserProfileUsername(dto.username()).ifPresentOrElse(profilePhotoMetadata -> {
			// if present, modify data
			profilePhotoMetadata.setUniqueKey(dto.uniqueKey());
			profilePhotoMetadata.setLocation(dto.location());
			profilePhotoMetadataRepo.save(profilePhotoMetadata);
		}, () -> {
			// or else, save new data
			ProfilePhotoMetadata newMetaData = new ProfilePhotoMetadata(dto);
			profilePhotoMetadataRepo.save(newMetaData);
		});

	}

	public ProfilePhotoMetadataDto getProfilePhotoMetadata(String username) {

		ProfilePhotoMetadata metadata = profilePhotoMetadataRepo.findByUserProfileUsername(username)
				.orElseThrow(() -> new ProfileException("", HttpStatus.NOT_FOUND));

		return new ProfilePhotoMetadataDto(username, metadata.getUniqueKey(), metadata.getLocation());

	}

}
