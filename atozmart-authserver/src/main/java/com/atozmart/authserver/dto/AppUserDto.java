package com.atozmart.authserver.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.atozmart.authserver.entity.AppRole;
import com.atozmart.authserver.entity.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppUserDto implements UserDetails, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;

	private String password;

	private String mail;

	private String mobileNo;

	private Set<String> roles;

	private Boolean emailVerified;

	private Boolean mobileNoVerified;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public AppUserDto(AppUser appUser) {
		this.username = appUser.getUsername();
		this.password = appUser.getPassword();
		this.mail = appUser.getMail();
		this.mobileNo = appUser.getMobileNo();
		this.emailVerified = appUser.getEmailVerified();
		this.mobileNoVerified = appUser.getMobileNoVerified();
		this.roles = appUser.getRoles().stream().map(AppRole::getRole).collect(Collectors.toSet());
	}

	@JsonIgnore
	@Override
	public Collection<SimpleGrantedAuthority> getAuthorities() {

		if (roles.isEmpty())
			return Collections.emptyList();

		return this.roles.stream().map(roleName -> roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName)
				.map(SimpleGrantedAuthority::new)
				.toList();
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public Boolean getMobileNoVerified() {
		return mobileNoVerified;
	}

	public void setMobileNoVerified(Boolean mobileNoVerified) {
		this.mobileNoVerified = mobileNoVerified;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
