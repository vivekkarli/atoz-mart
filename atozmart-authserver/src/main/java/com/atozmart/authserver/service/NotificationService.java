package com.atozmart.authserver.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.atozmart.authserver.AuthServerUtil;
import com.atozmart.authserver.configuration.AtozMartConfig;
import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dao.NotificationDao;
import com.atozmart.authserver.dto.ForgotPasswordRequest;
import com.atozmart.authserver.dto.MailContentDto;
import com.atozmart.authserver.dto.ResetPasswordRequest;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerification;
import com.atozmart.authserver.entity.PasswordResetToken;
import com.atozmart.authserver.exception.AuthServerException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationDao notificationDao;

	private final AppUserDao appUserDao;

	private final StreamBridge streamBridge;

	private final AtozMartConfig atozMartConfig;

	private final PasswordEncoder passwordEncoder;

	public void sendEmailConfirmationMail(String username, String email) {
		// step-1 generate random code
		String code = UUID.randomUUID().toString();
		String emailBody = AuthServerUtil.getConfirmEmailContent(username, code);

		// step-2 update table
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setCode(code);
		emailVerification.setUsername(username);
		emailVerification.setEmail(email);
		emailVerification.setNotificationSent(false);
		notificationDao.updateEmailVerificationTable(emailVerification);

		// step-3 call notification service
		try {
			sendEmail(email, emailBody);
		} catch (Exception ex) {
			log.debug("couldn't send email verification mail, {}", ex.getMessage());
		}

	}

	public ResponseEntity<String> confirmEmail(String token) {
		notificationDao.confirmEmail(token);
		return new ResponseEntity<>("email verified successfully", HttpStatus.ACCEPTED);
	}

	@Transactional
	public void handleForgotPassword(ForgotPasswordRequest request) {

		// check if there is already an un-expired token
		try {
			PasswordResetToken passwordResetToken = notificationDao
					.findPasswordResetTokenByUsername(request.username());
			if (!passwordResetToken.isExpired()) {
				String encodedToken = getEncodedToken(passwordResetToken.getToken());
				var resetLink = getResetLink(atozMartConfig.getBaseUrl(), encodedToken);
				log.info("reset link: {}", resetLink);
				mailResetLink(request.username(), resetLink.toString());
				return;
			}
		} catch (AuthServerException ex) {
			log.debug("exception finding existing token", ex.getMessage());
		}

		String token = UUID.randomUUID().toString();
		storeResetToken(request.username(), token, atozMartConfig.getResetTokenExpiry());
		var resetLink = getResetLink(atozMartConfig.getBaseUrl(), getEncodedToken(token));
		log.info("reset link: {}", resetLink);
		mailResetLink(request.username(), resetLink.toString());
	}

	@Transactional
	public void resetPassword(String encodedToken, ResetPasswordRequest request) {

		String token = getDecodedToken(encodedToken);
		PasswordResetToken passwordResetToken = notificationDao.findPasswordResetTokenByToken(token);

		if (passwordResetToken.isExpired()) {
			log.info("link expired");
			notificationDao.deletePasswordResetToken(passwordResetToken);
			throw new AuthServerException("link expired", HttpStatus.GONE);
		}

		AppUser appUser = notificationDao.loadUserByUsername(passwordResetToken.getAppUser().getUsername());
		appUser.setPassword(passwordEncoder.encode(request.newPassword()));

		appUserDao.updateUser(appUser);
		notificationDao.deletePasswordResetToken(passwordResetToken);
	}

	private void mailResetLink(String username, String resetLink) {
		String email = getEmail(username);
		String emailBody = AuthServerUtil.getPasswordResetEmailContent(username, resetLink);
		try {
			sendEmail(email, emailBody);
		} catch (Exception ex) {
			log.debug("exception sending password reset mail, {}", ex.getMessage());
			throw new AuthServerException("couldn't mail reset link at the moment, please try again later",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String getEncodedToken(String token) {
		var encodedTokenBytes = Base64.getEncoder().encode(token.getBytes());
		return new String(encodedTokenBytes);
	}

	private String getDecodedToken(String encodedToken) {
		var decodedTokenBytes = Base64.getDecoder().decode(encodedToken.getBytes());
		return new String(decodedTokenBytes);
	}

	private URI getResetLink(String baseUrl, String encodedToken) {
		return UriComponentsBuilder.fromUriString(baseUrl).path("reset-password").queryParam("token", encodedToken)
				.build().toUri();
	}

	private void storeResetToken(String username, String token, Long expiry) {
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setToken(token);
		AppUser appUser = new AppUser();
		appUser.setUsername(username);
		passwordResetToken.setAppUser(appUser);
		passwordResetToken.setExpiresAt(LocalDateTime.now().plusMinutes(expiry));
		notificationDao.savePasswordResetToken(passwordResetToken);
	}

	private void sendEmail(String email, String body) {
		MailContentDto mailContentDto = new MailContentDto(email, null, body, null);
		streamBridge.send("sendEmail-out-0", mailContentDto);
	}

	private String getEmail(String username) throws UsernameNotFoundException {
		return notificationDao.loadUserByUsername(username).getMail();
	}

}
