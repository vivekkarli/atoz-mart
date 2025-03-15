package com.atozmart.gatewayserver.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

class CustomAtozmartAuthentication implements Authentication {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String token;
	
	private final List<GrantedAuthority> authorities;

	public CustomAtozmartAuthentication(String token, List<GrantedAuthority> authorities) {
		this.token = token;
		this.authorities = authorities != null ? authorities : Collections.emptyList();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities; 
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return null;
	}
}
