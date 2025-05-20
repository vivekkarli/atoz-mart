package com.atozmart.authserver.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AuthorizeResponse {
	private boolean valid;
	private String username;
	private String email;
	private Date expiresAt;
	private List<String> roles;

	
}
