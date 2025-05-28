package com.atozmart.profile.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "username" })
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "unx_username_addType", columnNames = { "username",
		"addressType" }))
public class UserAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "username", nullable = false)
	private UserProfile username;

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

	@Override
	public String toString() {
		return "UserAddress [id=" + id + ", username=" + username.getUsername() + ", addressType=" + addressType
				+ ", defaultAddress=" + defaultAddress + ", addLine1=" + addLine1 + ", addLine2=" + addLine2
				+ ", addLine3=" + addLine3 + ", pincode=" + pincode + ", country=" + country + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
