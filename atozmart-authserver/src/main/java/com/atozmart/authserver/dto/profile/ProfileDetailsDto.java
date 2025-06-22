package com.atozmart.authserver.dto.profile;

import java.util.List;

import jakarta.validation.Valid;

public record ProfileDetailsDto(

		@Valid BasicDetailsDto basicDetailsDto,

		@Valid List<AddressDetailsDto> addressDetailsDto) {

}
