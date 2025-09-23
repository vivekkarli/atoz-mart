package com.atozmart.authserver.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
		
		@NotEmpty(message = "provide old password")
		String oldPassword, 
		
		@NotEmpty(message = "provide new password")
		@Size(min = 4, message = "password criteria not matched")
		String newPassword) {

}
