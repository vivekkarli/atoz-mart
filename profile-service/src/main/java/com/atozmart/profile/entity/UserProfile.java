package com.atozmart.profile.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = {"addresses"})
@ToString(exclude = {"addresses"})
@Entity
@DynamicInsert
@DynamicUpdate
public class UserProfile {

	@Id
	private String username;

	private String firstName;
	private String lastName;
	private String mail;
	private String mobileNo;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;
	
	@OneToMany(mappedBy = "username", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<UserAddress> addresses;
	
}
