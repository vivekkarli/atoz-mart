package com.atozmart.authserver.dao;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerificationToken;
import com.atozmart.authserver.entity.PasswordResetToken;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;
import com.atozmart.authserver.repository.EmailVerificationTokenRepository;
import com.atozmart.authserver.repository.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationDao {

	private final AppUserRepository appUserRepository;

	private final EmailVerificationTokenRepository emailVerificationTokenRepo;

	private final PasswordResetTokenRepository passwordResetTokenRepo;

	public EmailVerificationToken findEmailVerificationTokenByToken(String token) {
		return emailVerificationTokenRepo.findByToken(token)
				.orElseThrow(() -> new AuthServerException("invalid email verification link", HttpStatus.BAD_REQUEST));
	}

	public PasswordResetToken findPasswordResetTokenByToken(String token) {
		return passwordResetTokenRepo.findByToken(token).orElseThrow(
				() -> new AuthServerException("password reset link invalid or expired", HttpStatus.BAD_REQUEST));
	}

	public void saveEmailVerificationToken(EmailVerificationToken emailVerificationToken) {
		emailVerificationTokenRepo.save(emailVerificationToken);
	}

	public void savePasswordResetToken(PasswordResetToken passwordResetToken) {
		passwordResetTokenRepo.save(passwordResetToken);
	}

	public PasswordResetToken findPasswordResetTokenByUsername(String username) {
		AppUser appUser = new AppUser();
		appUser.setUsername(username);

		return passwordResetTokenRepo.findByAppUser(appUser)
				.orElseThrow(() -> new AuthServerException("token not found", HttpStatus.BAD_REQUEST));

	}

	public EmailVerificationToken findEmailVerificationTokenUsername(String username) {
		AppUser appUser = new AppUser();
		appUser.setUsername(username);

		return emailVerificationTokenRepo.findByAppUser(appUser)
				.orElseThrow(() -> new AuthServerException("token not found", HttpStatus.BAD_REQUEST));
	}

	public void deletePasswordResetToken(PasswordResetToken passwordResetToken) {
		passwordResetTokenRepo.delete(passwordResetToken);
	}

	public void deleteEmailVerificationToken(EmailVerificationToken emailVerificationToken) {
		emailVerificationTokenRepo.delete(emailVerificationToken);
	}

	public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
	}

}
