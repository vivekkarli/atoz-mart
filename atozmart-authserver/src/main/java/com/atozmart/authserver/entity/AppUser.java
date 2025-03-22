package com.atozmart.authserver.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.lang.Arrays;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AppUser implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String username;
	
	private String password;
	
	private String mail;
	
	private String mobileNo;
	
	private String roles;
	
	private Boolean emailVerified;
	
	private Boolean mobileNoVerified;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updateAt; 

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		List<String> roleLst = Arrays.asList(roles.split(","));
		
		if(roleLst.isEmpty())
			return Collections.emptyList();
		
		return roleLst.stream()
				.map(role -> role.startsWith("ROLE_") ? role : "ROLE_"+role)
				.map(SimpleGrantedAuthority::new)
				.toList();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
