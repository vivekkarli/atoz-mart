package com.atozmart.profile.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.atozmart.profile.entity.AddressTypeEnum;
import com.atozmart.profile.entity.UserAddress;
import com.atozmart.profile.entity.UserAddressCompKey;
import com.atozmart.profile.entity.UserProfile;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.repository.UserAddressRepository;
import com.atozmart.profile.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProfileDao {

	private final UserProfileRepository userProfileRepo;

	private final UserAddressRepository userAddressRepo;

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

}
