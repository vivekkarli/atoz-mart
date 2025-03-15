package com.atozmart.authserver.dto;

import java.util.List;

import lombok.Data;

@Data
public class AuthorizeResponse {
	private boolean valid;
	private List<String> roles;

	
}
