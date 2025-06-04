package com.atozmart.profile.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.atozmart.profile.entity.UserProfile;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.repository.UserAddressRepository;
import com.atozmart.profile.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ProfileDao {

	private final UserProfileRepository userProfileRepo;

	private final UserAddressRepository userAddressRepo;

	public UserProfile getProfileDetails(String username) {

		return userProfileRepo.findById(username)
				.orElseThrow(() -> new ProfileException("no profile found", HttpStatus.NOT_FOUND));

	}

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

}
