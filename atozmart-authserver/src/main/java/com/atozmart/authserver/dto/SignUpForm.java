package com.atozmart.authserver.dto;

import jakarta.validation.constraints.Email;

public record SignUpForm(String username, String firstName, String lastName, String password,
		
		@Email(message = "invalid email format")
		String mail,
		String mobileNo) {

}
