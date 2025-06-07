package com.atozmart.profile.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressDetails {

	@Pattern(regexp = "home|work|others", message = "address type should only be home/work/others")
	private String addressType;

	private String addressDesc;

	private boolean defaultAddress;

	@NotEmpty(message = "address line 1 is mandatory")
	private String addLine1;

	private String addLine2;

	private String addLine3;

	@NotEmpty(message = "pincode is mandatory")
	private String pincode;

	private String country;

}
