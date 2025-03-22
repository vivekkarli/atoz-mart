package com.atozmart.authserver.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class EmailVerification {
	
	@Id
	@Column(name = "verification_code")
	private String code;
	
	private String username;
	
	private String email;
	
	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	

}
