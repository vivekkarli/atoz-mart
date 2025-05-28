package com.atozmart.authserver.dto.profile;

import lombok.Data;

@Data
public class AddressDetails {

	private String addressType;
	
	private boolean defaultAddress;

	private String addLine1;

	private String addLine2;

	private String addLine3;

	private String pincode;
	
	private String country;
	
}
