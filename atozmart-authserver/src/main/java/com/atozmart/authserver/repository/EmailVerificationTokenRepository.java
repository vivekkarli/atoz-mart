package com.atozmart.authserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String>{
	
	Optional<EmailVerificationToken> findByToken(String token);
	
	Optional<EmailVerificationToken> findByAppUser(AppUser appUser);

}
