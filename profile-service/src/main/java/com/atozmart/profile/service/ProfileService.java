package com.atozmart.profile.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atozmart.profile.dao.ProfileDao;
import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.entity.UserProfile;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.util.ProfileMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {

	private final ProfileDao profileDao;
	
	private final AuthServerFeignClient authServerFeignClient;

	public ProfileDetails getProfileDetails(String username) {

		UserProfile userProfile = profileDao.getProfileDetails(username);
		log.debug("userProfile: {}", userProfile);

		return ProfileMapper.mapToProfileDetails(userProfile);

	}

	public void addNewProfile(String username, ProfileDetails profileDetails) {

		UserProfile userProfile = ProfileMapper.mapToUserProfile(username, profileDetails);

		profileDao.addOrUpdateProfile(userProfile);

	}

	@Transactional
	public void editProfileDetails(String username, ProfileDetails profileDetails) throws ProfileException {

		if (!profileDao.doesProfileExists(username))
			throw new ProfileException("profile doesn't exist with username: %s".formatted(username), HttpStatus.NOT_FOUND);

		var isUsernameChanged = !profileDetails.getBasicDetails().getUsername().equals(username);

		// case-1 if username is not changed -> update profile
		if (!isUsernameChanged) {
			UserProfile userProfile = ProfileMapper.mapToUserProfile(username, profileDetails);
			profileDao.addOrUpdateProfile(userProfile);
			return;
		}

		// case-2 if username is changed
		// check if new username is already taken
		if (profileDao.doesProfileExists(profileDetails.getBasicDetails().getUsername()))
			throw new ProfileException(
					"username %s already taken".formatted(profileDetails.getBasicDetails().getUsername()), HttpStatus.BAD_REQUEST);

		// create new profile
		UserProfile userProfile = ProfileMapper.mapToUserProfile(profileDetails.getBasicDetails().getUsername(),
				profileDetails);
		profileDao.addOrUpdateProfile(userProfile);

		// update the new username in auth server also --> synchronous call
		authServerFeignClient.updateBasicDetails(username, profileDetails.getBasicDetails());

		// delete old profile
		profileDao.deleteProfile(username);

	}

}
