package com.atozmart.authserver.dto.profile;

public record AddressDetailsDto(

		String addressType,

		String addressDesc,

		boolean defaultAddress,

		String addLine1,

		String addLine2,

		String addLine3,

		String pincode,

		String country) {

}
