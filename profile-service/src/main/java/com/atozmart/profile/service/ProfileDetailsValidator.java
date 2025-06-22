package com.atozmart.profile.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.profile.dto.AddressDetailsDto;
import com.atozmart.profile.dto.ProfileDetailsDto;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.util.AddressTypeEnum;

@Service
public class ProfileDetailsValidator {

	public void validateProfileDetails(ProfileDetailsDto profileDetailsDto) throws ProfileException {

		Optional.ofNullable(profileDetailsDto.addressDetailsDto()).ifPresent(addressDetails -> {

			validateDefaultAddress(addressDetails);
			validateOthersDescription(addressDetails);

		});

	}

	private void validateDefaultAddress(List<AddressDetailsDto> addresses) {

		long defaultCount = addresses.stream().filter(AddressDetailsDto::defaultAddress).count();

		if (defaultCount > 1)
			throw new ProfileException("only one defualt address is allowed", HttpStatus.BAD_REQUEST);

	}

	private void validateOthersDescription(List<AddressDetailsDto> addresses) {
		addresses.forEach(address -> {

			if (address.addressType().equals(AddressTypeEnum.OTHERS.getAddressType())
					&& (address.addressDesc() == null || address.addressDesc().isBlank())) {
				throw new ProfileException("address description is mandatory if address type is others",
						HttpStatus.BAD_REQUEST);
			}

		});
	}

}
