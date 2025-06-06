package com.atozmart.profile.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.profile.dto.AddressDetails;
import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.entity.AddressTypeEnum;
import com.atozmart.profile.exception.ProfileException;

@Service
public class ProfileDetailsValidator {

	public void validateProfileDetails(ProfileDetails profileDetails) throws ProfileException {

		Optional.ofNullable(profileDetails.getAddressDetails()).ifPresent(addressDetails -> {

			validateDefaultAddress(addressDetails);
			validateOthersDescription(addressDetails);

		});

	}

	private void validateDefaultAddress(List<AddressDetails> addresses) {

		long defaultCount = addresses.stream().filter(AddressDetails::isDefaultAddress).count();

		if (defaultCount > 1)
			throw new ProfileException("only one defualt address is allowed", HttpStatus.BAD_REQUEST);

	}

	private void validateOthersDescription(List<AddressDetails> addresses) {
		addresses.forEach(address -> {

			if (address.getAddressType().equals(AddressTypeEnum.OTHERS.getAddressType())
					&& (address.getAddressDesc() == null || address.getAddressDesc().isBlank())) {
				throw new ProfileException("address description is mandatory if address type is others",
						HttpStatus.BAD_REQUEST);
			}

		});
	}

}
