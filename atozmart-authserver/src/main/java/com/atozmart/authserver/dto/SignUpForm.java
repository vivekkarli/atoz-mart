package com.atozmart.authserver.dto;

public record SignUpForm(String username, String firstName, String lastName, String password, String mail,
		String mobileNo) {

}
