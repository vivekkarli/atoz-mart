package com.atozmart.authserver.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.atozmart.authserver.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
	
	Optional<AppUser> findByUsername(String username);
	
	@Modifying
	@Query("""
			update AppUser a
			set
			  a.username = :newUsername,
			  a.mail = :newMail,
			  a.mobileNo = :newMobileNo,
			  a.updatedAt = :updatedAt
			where
			  a.username = :username """)
	void updateBasicDetails(String newUsername, String newMail, String newMobileNo, LocalDateTime updatedAt, String username);

}
