package com.atozmart.profile.dao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.atozmart.profile.entity.UserProfile;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ProfileDao {

	private final UserProfileRepository userProfileRepo;

	public UserProfile getProfileDetails(String username) {

		UserProfile userProfile = userProfileRepo.findById(username)
				.orElseThrow(() -> new ProfileException("no profile found", HttpStatus.NOT_FOUND));
		return userProfile;

	}
	
	public void addNewProfile(UserProfile userProfile) {
		userProfileRepo.save(userProfile);
	}

}
