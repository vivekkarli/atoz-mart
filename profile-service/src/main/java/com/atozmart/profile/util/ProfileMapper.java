package com.atozmart.profile.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.atozmart.profile.configuration.GeneralConfig;
import com.atozmart.profile.dto.AddressDetails;
import com.atozmart.profile.dto.BasicDetails;
import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.entity.AddressTypeEnum;
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

		Optional.ofNullable(profileDetails.getBasicDetails()).ifPresent(basicDetails -> {
			userProfile.setFirstName(basicDetails.getFirstName());
			userProfile.setLastName(basicDetails.getLastName());
			userProfile.setMail(basicDetails.getMail());
			userProfile.setMobileNo(basicDetails.getMobileNo());
		});

		Optional.ofNullable(profileDetails.getAddressDetails()).ifPresentOrElse(profileAddresses -> {
			Set<UserAddress> userAddresses = profileAddresses.stream().map(address -> {
				UserAddress userAddress = MODEL_MAPPER.map(address, UserAddress.class);
				userAddress.setUsername(username);
				userAddress.setAddressType(AddressTypeEnum.fromString(address.getAddressType()));
				return userAddress;
			}).collect(Collectors.toSet());
			log.debug("userAddresses: {}", userAddresses);
			userProfile.setAddresses(userAddresses);
		}, () -> userProfile.setAddresses(Collections.emptySet()));

		log.debug("userProfile: {}", userProfile);

		return userProfile;
	}

	public static ProfileDetails mapToProfileDetails(UserProfile userProfile) {

		Set<UserAddress> userAddresses = userProfile.getAddresses();
		log.debug("userAddress: {}", userAddresses);

		List<AddressDetails> addressDetails = userAddresses.stream().map(userAddress -> {
			AddressDetails addressDetailsTemp = MODEL_MAPPER.map(userAddress, AddressDetails.class);
			addressDetailsTemp.setAddressType(userAddress.getAddressType().getAddressType());
			return addressDetailsTemp;
		}).toList();

		BasicDetails basicDetails = MODEL_MAPPER.map(userProfile, BasicDetails.class);

		ProfileDetails profileDetails = new ProfileDetails();
		profileDetails.setBasicDetails(basicDetails);
		profileDetails.setAddressDetails(addressDetails);
		return profileDetails;
	}

}
