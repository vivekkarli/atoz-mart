package com.atozmart.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;

public record BasicDetailsDto(

		@Null(message = "username name should be null") 
		String username, 
		String firstName, String lastName,

		@Email(message = "invalid email format") 
		String mail, 
		String mobileNo) {

}
