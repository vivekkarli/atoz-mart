package com.atozmart.authserver.dao;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerification;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;
import com.atozmart.authserver.repository.EmailVerificationRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class NotificationDao {

	private AppUserRepository appUserRepository;

	private EmailVerificationRepository emailVerificationRepo;

	@Transactional
	public void verifyEmail(String code) {

		Optional<EmailVerification> emailVerifyOpt = emailVerificationRepo.findById(code);

		EmailVerification emailVerify = emailVerifyOpt
				.orElseThrow(() -> new AuthServerException("invalid verification link", HttpStatus.BAD_REQUEST));

		AppUser appUser = appUserRepository.findById(emailVerify.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		appUser.setEmailVerified(true);

		appUserRepository.save(appUser);

		emailVerificationRepo.deleteById(code);

	}

	public void updateEmailVerificationTable(EmailVerification emailVerification) {
		emailVerificationRepo.save(emailVerification);
	}

}
