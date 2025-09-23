package com.atozmart.authserver.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String token;

	@OneToOne
	@JoinColumn(name = "username")
	private AppUser appUser;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime expiresAt;

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}

}
