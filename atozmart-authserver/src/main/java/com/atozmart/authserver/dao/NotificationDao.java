package com.atozmart.authserver.dao;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerification;
import com.atozmart.authserver.entity.PasswordResetToken;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;
import com.atozmart.authserver.repository.EmailVerificationRepository;
import com.atozmart.authserver.repository.PasswordResetTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationDao {

	private final AppUserRepository appUserRepository;

	private final EmailVerificationRepository emailVerificationRepo;

	private final PasswordResetTokenRepository passwordResetTokenRepo;

	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void confirmEmail(String token) {

		Optional<EmailVerification> emailVerifyOpt = emailVerificationRepo.findById(token);

		EmailVerification emailVerify = emailVerifyOpt
				.orElseThrow(() -> new AuthServerException("invalid email verification link", HttpStatus.BAD_REQUEST));

		AppUser appUser = appUserRepository.findById(emailVerify.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		appUser.setEmailVerified(true);

		appUserRepository.save(appUser);

		emailVerificationRepo.deleteById(token);

	}

	@Transactional
	public void resetPassword(String token, String newPassword) {

		PasswordResetToken passwordResetToken = passwordResetTokenRepo.findByToken(token)
				.orElseThrow(() -> new AuthServerException("invalid password reset link", HttpStatus.BAD_REQUEST));

		if (passwordResetToken.isExpired()) {
			passwordResetTokenRepo.deleteById(passwordResetToken.getId());
			throw new AuthServerException("link expired", HttpStatus.GONE);
		}

		AppUser appUser = appUserRepository.findById(passwordResetToken.getAppUser().getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		appUser.setPassword(passwordEncoder.encode(newPassword));

		appUserRepository.save(appUser);

		passwordResetTokenRepo.deleteById(passwordResetToken.getId());

	}

	public void updateEmailVerificationTable(EmailVerification emailVerification) {
		emailVerificationRepo.save(emailVerification);
	}

	public void updatePasswordResetTokenTable(PasswordResetToken passwordResetToken) {
		passwordResetTokenRepo.save(passwordResetToken);
	}

	public PasswordResetToken findPasswordResetTokenByUsername(String username) {
		AppUser appUser = new AppUser();
		appUser.setUsername(username);

		return passwordResetTokenRepo.findByAppUser(appUser)
				.orElseThrow(() -> new AuthServerException("token not found", HttpStatus.BAD_REQUEST));

	}

	public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
	}

}
