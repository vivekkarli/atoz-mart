package com.atozmart.profile.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.profile.dao.ProfileDao;
import com.atozmart.profile.dto.ProfileDetailsDto;
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
	
	public ProfileDetailsDto getProfileDetails(String username) {

		UserProfile userProfile = profileDao.getProfileDetails(username);
		log.debug("userProfile: {}", userProfile);

		return ProfileMapper.mapToProfileDetails(userProfile);

	}

	public void addNewProfile(String username, ProfileDetailsDto profileDetailsDto) {

		if (profileDao.doesProfileExists(username))
			throw new ProfileException("profile already exists with username: %s".formatted(username),
					HttpStatus.NOT_FOUND);

		UserProfile userProfile = ProfileMapper.mapToUserProfile(username, profileDetailsDto);

		profileDao.addOrUpdateProfile(userProfile);

	}

	public void editProfileDetails(String username, ProfileDetailsDto profileDetailsDto) throws ProfileException {

		if (!profileDao.doesProfileExists(username))
			throw new ProfileException("profile doesn't exists with username: %s".formatted(username),
					HttpStatus.NOT_FOUND);

		UserProfile userProfile = ProfileMapper.mapToUserProfile(username, profileDetailsDto);
		profileDao.addOrUpdateProfile(userProfile);

	}

	public void deleteAddress(String username, String addressType) {

		if (!profileDao.doesProfileExists(username))
			throw new ProfileException("profile doesn't exists with username: %s".formatted(username),
					HttpStatus.NOT_FOUND);

		profileDao.deleteAddress(username, addressType);
	}

	public void changeDefaultTo(String username, String addressType) {
		
		profileDao.changeDefaultTo(username, addressType);
		
	}

}
