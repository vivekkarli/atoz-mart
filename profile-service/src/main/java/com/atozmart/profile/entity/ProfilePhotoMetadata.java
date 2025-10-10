package com.atozmart.profile.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.atozmart.profile.dto.ProfilePhotoMetadataDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor
public class ProfilePhotoMetadata {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "username", referencedColumnName = "username")
	private UserProfile userProfile;
	
	private String uniqueKey;
	
	private String location;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;
	
	
	public ProfilePhotoMetadata(ProfilePhotoMetadataDto dto) {
		
		UserProfile profile = new UserProfile();
		profile.setUsername(dto.username());
		
		this.userProfile = profile;
		this.uniqueKey = dto.uniqueKey();
		this.location = dto.location();
	}
	

}
