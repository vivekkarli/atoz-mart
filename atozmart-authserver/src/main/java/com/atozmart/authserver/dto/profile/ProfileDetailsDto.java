package com.atozmart.authserver.dto.profile;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileDetailsDto(

		@JsonProperty("basicDetails")
		BasicDetailsDto basicDetailsDto,

		@JsonProperty("addressDetails")
		List<AddressDetailsDto> addressDetailsDto) {

}
