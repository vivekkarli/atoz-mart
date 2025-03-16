package com.atozmart.gatewayserver.authentication;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.atozmart.gatewayserver.dto.AuthorizeResponse;

import lombok.Data;

@Data
public class AtozmartAuthenticationToken extends UsernamePasswordAuthenticationToken{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AuthorizeResponse authorizeResponse;
	 

	public AtozmartAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
		this.authorizeResponse = (AuthorizeResponse) principal;
	}
	
	public AtozmartAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
		this.authorizeResponse = (AuthorizeResponse) principal;
	}

}
