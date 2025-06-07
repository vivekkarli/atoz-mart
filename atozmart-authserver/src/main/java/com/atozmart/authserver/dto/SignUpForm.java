package com.atozmart.authserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record SignUpForm(
		
		@NotEmpty(message = "please enter a username")
		String username, 
		
		String firstName, 
		String lastName, 
		
		@NotEmpty(message = "please enter your password")
		String password,
		
		@Email(message = "invalid email format")
		String mail,
		
		String mobileNo) {

}
