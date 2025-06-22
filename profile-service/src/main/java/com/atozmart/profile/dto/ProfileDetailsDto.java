package com.atozmart.profile.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public record ProfileDetailsDto(

		@Valid 
		@JsonProperty("basicDetails")
		BasicDetailsDto basicDetailsDto,

		@Valid 
		@JsonProperty("addressDetails")
		List<AddressDetailsDto> addressDetailsDto) {

}
