package com.atozmart.authserver.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.atozmart.authserver.dto.AppUserDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
@DynamicUpdate
@DynamicInsert
public class AppUser {

	@Id
	private String username;

	private String password;

	private String mail;

	private String mobileNo;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "app_user_role", joinColumns = @JoinColumn(name = "username"), inverseJoinColumns = @JoinColumn(name = "role"))
	private Set<AppRole> roles;

	private Boolean emailVerified;

	private Boolean mobileNoVerified;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;

	public AppUser(AppUserDto appUserDto) {
		this.username = appUserDto.getUsername();
		this.password = appUserDto.getPassword();
		this.mail = appUserDto.getMail();
		this.mobileNo = appUserDto.getMobileNo();
		this.emailVerified = appUserDto.getEmailVerified();
		this.mobileNoVerified = appUserDto.getMobileNoVerified();
		this.roles = appUserDto.getRoles().stream().map(role -> new AppRole(role, null)).collect(Collectors.toSet());
	}

}
