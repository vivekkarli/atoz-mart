package com.atozmart.profile.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class BasicDetails {

	private String username;
	private String firstName;
	private String lastName;
	
	@Email(message = "invalid email format")
	private String mail;
	private String mobileNo;
	
}
