package com.atozmart.profile.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

@IdClass(UserAddressCompKey.class)
@Data
@Entity
public class UserAddress {

	@Id
	private String username;

	@Id
	private String addressType;

	private boolean defaultAddress;

	@Column(name = "add_line_1")
	private String addLine1;

	@Column(name = "add_line_2")
	private String addLine2;

	@Column(name = "add_line_3")
	private String addLine3;

	private String pincode;
	private String country;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;

}
