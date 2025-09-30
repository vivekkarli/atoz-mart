package com.atozmart.authserver.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.configuration.AtozMartConfig;
import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dao.NotificationDao;
import com.atozmart.authserver.dto.ForgotPasswordRequest;
import com.atozmart.authserver.dto.MailContentDto;
import com.atozmart.authserver.dto.ResetPasswordRequest;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerificationToken;
import com.atozmart.authserver.entity.PasswordResetToken;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.util.AuthServerConstants;
import com.atozmart.authserver.util.AuthServerUtil;

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

	private final AuthServerUtil authServerUtil;

	@Transactional
	public void handleEmailVerfication(String username, String email) {

		// check if there is already an un-expired token
		try {
			EmailVerificationToken emailVerificationToken = notificationDao
					.findEmailVerificationTokenUsername(username);
			if (!emailVerificationToken.isExpired()) {
				String encodedToken = authServerUtil.getEncodedToken(emailVerificationToken.getToken());
				var verifyLink = authServerUtil.getVerifyLink(atozMartConfig.baseUrl(), encodedToken);
				log.info("verify link: {}", verifyLink);
				mailVerifyLink(username, verifyLink.toString());
				return;
			}
		} catch (AuthServerException ex) {
			log.debug("exception finding existing token", ex.getMessage());
		}

		sendNewMailVerifiyLink(username);
	}

	private void sendNewMailVerifiyLink(String username) {
		String token = UUID.randomUUID().toString();
		storeVerifyToken(username, token, atozMartConfig.emailVerifyTokenExpiry());
		URI verifyLink = authServerUtil.getVerifyLink(atozMartConfig.baseUrl(),
				authServerUtil.getEncodedToken(token));
		mailVerifyLink(username, verifyLink.toString());
	}
	
	@Async
	@Transactional
	public void sendNewMailVerifiyLinkAsync(String username) {
		sendNewMailVerifiyLink(username);
	}

	@Transactional
	public void handleForgotPassword(ForgotPasswordRequest request) {

		// check if there is already an un-expired token
		try {
			PasswordResetToken passwordResetToken = notificationDao
					.findPasswordResetTokenByUsername(request.username());
			if (!passwordResetToken.isExpired()) {
				String encodedToken = authServerUtil.getEncodedToken(passwordResetToken.getToken());
				var resetLink = authServerUtil.getResetLink(atozMartConfig.baseUrl(), encodedToken);
				log.info("reset link: {}", resetLink);
				mailResetLink(request.username(), resetLink.toString());
				return;
			}
		} catch (AuthServerException ex) {
			log.debug("exception finding existing token", ex.getMessage());
		}

		sendNewPwdResetLink(request);
	}

	private void sendNewPwdResetLink(ForgotPasswordRequest request) {
		String token = UUID.randomUUID().toString();
		storeResetToken(request.username(), token, atozMartConfig.pwdResetTokenExpiry());
		var resetLink = authServerUtil.getResetLink(atozMartConfig.baseUrl(), authServerUtil.getEncodedToken(token));
		log.info("reset link: {}", resetLink);
		mailResetLink(request.username(), resetLink.toString());
	}

	@Transactional
	public void confirmEmail(String encodedToken) {
		String token = authServerUtil.getDecodedToken(encodedToken);
		EmailVerificationToken emailVerificationToken = notificationDao.findEmailVerificationTokenByToken(token);
		if (emailVerificationToken.isExpired()) {
			log.info("link expired");
			notificationDao.deleteEmailVerificationToken(emailVerificationToken);
			throw new AuthServerException("link expired", HttpStatus.GONE);
		}

		AppUser appUser = notificationDao.loadUserByUsername(emailVerificationToken.getAppUser().getUsername());
		appUser.setEmailVerified(true);

		appUserDao.updateUser(appUser);
		notificationDao.deleteEmailVerificationToken(emailVerificationToken);
	}

	@Transactional
	public void resetPassword(String encodedToken, ResetPasswordRequest request) {

		String token = authServerUtil.getDecodedToken(encodedToken);
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

	private void storeVerifyToken(String username, String token, Long expiry) {
		EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
		emailVerificationToken.setToken(token);
		AppUser appUser = new AppUser();
		appUser.setUsername(username);
		emailVerificationToken.setAppUser(appUser);
		emailVerificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(expiry));
		notificationDao.saveEmailVerificationToken(emailVerificationToken);
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

	private void mailResetLink(String username, String resetLink) {
		String email = getEmail(username);
		String emailBody = AuthServerConstants.getPasswordResetEmailContent(username, resetLink);
		try {
			sendEmail(email, emailBody);
		} catch (Exception ex) {
			log.debug("exception sending password reset mail, {}", ex.getMessage());
			throw new AuthServerException("couldn't mail reset link at the moment, please try again later",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void mailVerifyLink(String username, String resetLink) {
		String email = getEmail(username);
		String emailBody = AuthServerConstants.getConfirmEmailContent(username, resetLink);
		try {
			sendEmail(email, emailBody);
		} catch (Exception ex) {
			log.debug("exception sending password reset mail, {}", ex.getMessage());
			throw new AuthServerException("couldn't mail verify link at the moment, please try again later",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendEmail(String email, String body) {
		MailContentDto mailContentDto = new MailContentDto(email, null, body, null);
		streamBridge.send("sendEmail-out-0", mailContentDto);
	}

	private String getEmail(String username) throws UsernameNotFoundException {
		return notificationDao.loadUserByUsername(username).getMail();
	}

}
