package com.atozmart.authserver.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginForm (
		
		@NotEmpty(message = "please enter your username")
		String username, 
		
		@NotEmpty(message = "please enter your password")
		String password){

}
