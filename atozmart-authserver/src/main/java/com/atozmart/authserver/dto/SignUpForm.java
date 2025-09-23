package com.atozmart.authserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record SignUpForm(
		
		@NotEmpty(message = "please enter a username")
		String username, 
		
		String firstName, 
		String lastName, 
		
		@NotEmpty(message = "please enter your password")
		@Size(min = 4, message = "password criteria not matched")
		String password,
		
		@Email(message = "invalid email format")
		String mail,
		
		String mobileNo) {

}
