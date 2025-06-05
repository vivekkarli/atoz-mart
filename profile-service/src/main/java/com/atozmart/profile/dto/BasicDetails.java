package com.atozmart.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class BasicDetails {

	@Null(message = "username name should be null")
	private String username;
	private String firstName;
	private String lastName;
	
	@Email(message = "invalid email format")
	private String mail;
	private String mobileNo;
	
}
