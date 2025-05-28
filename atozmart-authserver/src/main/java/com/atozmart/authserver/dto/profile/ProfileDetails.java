package com.atozmart.authserver.dto.profile;

import java.util.List;

import lombok.Data;

@Data
public class ProfileDetails {
	
	private BasicDetails basicDetails;
	
	private List<AddressDetails> addressDetails;

}
