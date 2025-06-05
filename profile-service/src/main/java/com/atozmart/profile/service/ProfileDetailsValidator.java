package com.atozmart.profile.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.profile.dto.AddressDetails;
import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.exception.ProfileException;

@Service
public class ProfileDetailsValidator {

	public void validateProfileDetails(ProfileDetails profileDetails) throws ProfileException {

		long defaultCount = profileDetails.getAddressDetails().stream().filter(AddressDetails::isDefaultAddress)
				.count();

		if (defaultCount > 1)
			throw new ProfileException("only one defualt address is allowed", HttpStatus.BAD_REQUEST);

	}

}
