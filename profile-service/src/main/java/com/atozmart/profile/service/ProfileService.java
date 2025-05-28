package com.atozmart.profile.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.atozmart.profile.dao.ProfileDao;
import com.atozmart.profile.dto.AddressDetails;
import com.atozmart.profile.dto.BasicDetails;
import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.entity.UserAddress;
import com.atozmart.profile.entity.UserProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {

	private final ModelMapper modelMapper;

	private final ProfileDao profileDao;

	public ProfileDetails getProfileDetails(String username) {

		UserProfile userProfile = profileDao.getProfileDetails(username);
		log.debug("userProfile: {}", userProfile);

		Set<UserAddress> userAddresses = userProfile.getAddresses();
		log.debug("userAddress: {}", userAddresses);

		List<AddressDetails> addressDetails = userAddresses.stream()
				.map(userAddress -> modelMapper.map(userAddress, AddressDetails.class)).toList();

		BasicDetails basicDetails = modelMapper.map(userProfile, BasicDetails.class);

		ProfileDetails profileDetails = new ProfileDetails();
		profileDetails.setBasicDetails(basicDetails);
		profileDetails.setAddressDetails(addressDetails);

		return profileDetails;
	}

	public void addNewProfile(String username, ProfileDetails profileDetails) {

		UserProfile userProfile = new UserProfile();

		userProfile.setUsername(username);
		userProfile.setFirstName(profileDetails.getBasicDetails().getFirstName());
		userProfile.setLastName(profileDetails.getBasicDetails().getLastName());
		userProfile.setMail(profileDetails.getBasicDetails().getMail());
		userProfile.setMobileNo(profileDetails.getBasicDetails().getMobileNo());

		Set<UserAddress> userAddresses = profileDetails.getAddressDetails() == null ? Collections.EMPTY_SET
				: profileDetails.getAddressDetails().stream().map(address -> {
					UserAddress userAddress = modelMapper.map(address, UserAddress.class);
					userAddress.setUsername(userProfile);
					return userAddress;
				}).collect(Collectors.toSet());

		log.debug("userAddresses: {}", userAddresses);

		userProfile.setAddresses(userAddresses);
		log.debug("userProfile: {}", userProfile);

		profileDao.addNewProfile(userProfile);

	}

}
