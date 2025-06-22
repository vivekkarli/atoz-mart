package com.atozmart.profile.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record AddressDetailsDto(

		@Pattern(regexp = "home|work|others", message = "address type should only be home/work/others") 
		String addressType,

		String addressDesc,

		boolean defaultAddress,

		@NotEmpty(message = "address line 1 is mandatory") 
		String addLine1,

		String addLine2,

		String addLine3,

		@NotEmpty(message = "pincode is mandatory") 
		String pincode,

		String country) {

}
