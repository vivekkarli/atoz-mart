package com.atozmart.profile.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProfileDetails {
	
	private BasicDetails basicDetails;
	
	private List<AddressDetails> addressDetails;

}
