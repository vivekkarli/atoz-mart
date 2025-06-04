package com.atozmart.profile.dto;

import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ProfileDetails {
	
	@Valid
	private BasicDetails basicDetails;
	
	private List<AddressDetails> addressDetails;

}
