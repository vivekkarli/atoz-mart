package com.atozmart.authserver.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
	
	private Boolean notificationSent;
	
	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(insertable = false)
	@UpdateTimestamp
	private LocalDateTime modifiedAt;
	

}
