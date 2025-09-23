package com.atozmart.authserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	
	Optional<PasswordResetToken> findByToken(String token);
	
	Optional<PasswordResetToken> findByAppUser(AppUser appUser);

}
