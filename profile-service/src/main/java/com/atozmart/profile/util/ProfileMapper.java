package com.atozmart.profile.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.atozmart.profile.dto.AddressDetailsDto;
import com.atozmart.profile.dto.BasicDetailsDto;
import com.atozmart.profile.dto.ProfileDetailsDto;
import com.atozmart.profile.entity.UserAddress;
import com.atozmart.profile.entity.UserProfile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileMapper {

	private ProfileMapper() {
		throw new IllegalAccessError();
	}

	public static UserProfile mapToUserProfile(String username, ProfileDetailsDto profileDetailsDto) {

		UserProfile userProfile = new UserProfile();
		userProfile.setUsername(username);

		Optional.ofNullable(profileDetailsDto.basicDetailsDto()).ifPresent(basicDetails -> {
			userProfile.setFirstName(basicDetails.firstName());
			userProfile.setLastName(basicDetails.lastName());
			userProfile.setMail(basicDetails.mail());
			userProfile.setMobileNo(basicDetails.mobileNo());
		});

		Optional.ofNullable(profileDetailsDto.addressDetailsDto()).ifPresentOrElse(profileAddresses -> {
			Set<UserAddress> userAddresses = profileAddresses.stream().map(address -> {
				UserAddress userAddress = new UserAddress();
				userAddress.setUsername(username);
				userAddress.setAddressType(AddressTypeEnum.fromString(address.addressType()));
				userAddress.setAddressDesc(address.addressDesc());
				userAddress.setDefaultAddress(address.defaultAddress());
				userAddress.setAddLine1(address.addLine1());
				userAddress.setAddLine2(address.addLine2());
				userAddress.setAddLine3(address.addLine3());
				userAddress.setPincode(address.pincode());
				userAddress.setCountry(address.country());
				return userAddress;
			}).collect(Collectors.toSet());
			log.debug("userAddresses: {}", userAddresses);
			userProfile.setAddresses(userAddresses);
		}, () -> userProfile.setAddresses(Collections.emptySet()));

		log.debug("userProfile: {}", userProfile);

		return userProfile;
	}

	public static ProfileDetailsDto mapToProfileDetails(UserProfile userProfile) {

		Set<UserAddress> userAddresses = userProfile.getAddresses();
		log.debug("userAddress: {}", userAddresses);

		List<AddressDetailsDto> addressDetailsDto = userAddresses.stream().map(userAddress ->

		new AddressDetailsDto(userAddress.getAddressType().getAddressType(), userAddress.getAddressDesc(),
				userAddress.isDefaultAddress(), userAddress.getAddLine1(), userAddress.getAddLine2(),
				userAddress.getAddLine3(), userAddress.getPincode(), userAddress.getCountry())

		).toList();

		BasicDetailsDto basicDetailsDto = new BasicDetailsDto(userProfile.getUsername(), userProfile.getFirstName(),
				userProfile.getLastName(), userProfile.getMail(), userProfile.getMobileNo());

		return new ProfileDetailsDto(basicDetailsDto, addressDetailsDto);
	}

}
