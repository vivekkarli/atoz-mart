package com.atozmart.profile.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.atozmart.profile.configuration.GeneralConfig;
import com.atozmart.profile.dto.AddressDetails;
import com.atozmart.profile.dto.BasicDetails;
import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.entity.UserAddress;
import com.atozmart.profile.entity.UserProfile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileMapper {

	private ProfileMapper() {
		throw new IllegalAccessError();
	}

	private static final ModelMapper MODEL_MAPPER = new GeneralConfig().modelMapper();

	public static UserProfile mapToUserProfile(String username, ProfileDetails profileDetails) {

		UserProfile userProfile = new UserProfile();

		userProfile.setUsername(username);
		userProfile.setFirstName(profileDetails.getBasicDetails().getFirstName());
		userProfile.setLastName(profileDetails.getBasicDetails().getLastName());
		userProfile.setMail(profileDetails.getBasicDetails().getMail());
		userProfile.setMobileNo(profileDetails.getBasicDetails().getMobileNo());

		Set<UserAddress> userAddresses = profileDetails.getAddressDetails() == null ? Collections.emptySet()
				: profileDetails.getAddressDetails().stream().map(address -> {
					UserAddress userAddress = MODEL_MAPPER.map(address, UserAddress.class);
					userAddress.setUsername(username);
					return userAddress;
				}).collect(Collectors.toSet());

		log.debug("userAddresses: {}", userAddresses);

		userProfile.setAddresses(userAddresses);
		log.debug("userProfile: {}", userProfile);

		return userProfile;
	}

	public static ProfileDetails mapToProfileDetails(UserProfile userProfile) {

		Set<UserAddress> userAddresses = userProfile.getAddresses();
		log.debug("userAddress: {}", userAddresses);

		List<AddressDetails> addressDetails = userAddresses.stream()
				.map(userAddress -> MODEL_MAPPER.map(userAddress, AddressDetails.class)).toList();

		BasicDetails basicDetails = MODEL_MAPPER.map(userProfile, BasicDetails.class);

		ProfileDetails profileDetails = new ProfileDetails();
		profileDetails.setBasicDetails(basicDetails);
		profileDetails.setAddressDetails(addressDetails);
		return profileDetails;
	}

}
