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

import com.atozmart.authserver.cache.CacheHelper;
import com.atozmart.authserver.configuration.AtozMartConfig;
import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dao.NotificationDao;
import com.atozmart.authserver.dto.AppUserDto;
import com.atozmart.authserver.dto.ForgotPasswordRequest;
import com.atozmart.authserver.dto.MailContentDto;
import com.atozmart.authserver.dto.ResetPasswordRequest;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerificationToken;
import com.atozmart.authserver.entity.PasswordResetToken;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.util.AuthServerConstants;
import com.atozmart.authserver.util.AuthServerUtil;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
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

	private final EntityManager entityManager;

	private final CacheHelper appUserCacheHelper;

	private static final String CACHE_PREFIX;

	static {
		CACHE_PREFIX = "app-user::";
	}

	@Transactional
	public void handleEmailVerfication(String username, String email) {

		// check if there is already an un-expired token
		boolean doesTokenExist = sendUnExpiredVerifyToken(username, email);
		if (doesTokenExist)
			return;

		sendNewMailVerifiyLink(username, email);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	private boolean sendUnExpiredVerifyToken(String username, String email) {
		try {
			EmailVerificationToken emailVerificationToken = notificationDao
					.findEmailVerificationTokenUsername(username);
			if (!emailVerificationToken.isExpired()) {
				String encodedToken = authServerUtil.getEncodedToken(emailVerificationToken.getToken());
				URI verifyLink = authServerUtil.getVerifyLink(atozMartConfig.baseUrl(), encodedToken);
				log.info("existing verify link: {}", verifyLink);
				mailVerifyLink(username, email, verifyLink.toString());
				return true;
			}

			// if expired delete old token
			log.info("emailVerificationToken expired");
			notificationDao.deleteEmailVerificationToken(emailVerificationToken);
			entityManager.flush();
		} catch (AuthServerException ex) {
			log.debug("exception finding existing token", ex.getMessage());
		}

		return false;
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

		AppUserDto appUserDto = appUserDao.getUser(emailVerificationToken.getAppUser().getUsername());
		appUserDto.setEmailVerified(true);

		appUserDao.updateUser(appUserDto);
		notificationDao.deleteEmailVerificationToken(emailVerificationToken);

		/*
		 * cache the updated user
		 */
		appUserCacheHelper.cachePut(CACHE_PREFIX + appUserDto.getUsername(), appUserDto);
	}

	@Transactional
	public void handleForgotPassword(ForgotPasswordRequest request) {

		// check if there is already an un-expired token
		boolean doesTokenExist = sendunExpiredResetToken(request);
		if (doesTokenExist)
			return;

		sendNewPwdResetLink(request);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	private boolean sendunExpiredResetToken(ForgotPasswordRequest request) {
		try {
			PasswordResetToken passwordResetToken = notificationDao
					.findPasswordResetTokenByUsername(request.username());
			if (!passwordResetToken.isExpired()) {
				String encodedToken = authServerUtil.getEncodedToken(passwordResetToken.getToken());
				URI resetLink = authServerUtil.getResetLink(atozMartConfig.baseUrl(), encodedToken);
				log.info("existing reset link: {}", resetLink);
				mailResetLink(request.username(), resetLink.toString());
				return true;
			}

			// if expired delete old token
			log.info("passwordResetToken expired");
			notificationDao.deletePasswordResetToken(passwordResetToken);
			entityManager.flush();
		} catch (AuthServerException ex) {
			log.debug("exception finding existing token", ex.getMessage());
		}

		return false;
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

		AppUserDto appUserDto = appUserDao.getUser(passwordResetToken.getAppUser().getUsername());
		appUserDto.setPassword(passwordEncoder.encode(request.newPassword()));

		appUserDao.updateUser(appUserDto);
		notificationDao.deletePasswordResetToken(passwordResetToken);

		/*
		 * cache the updated user
		 */
		appUserCacheHelper.cachePut(CACHE_PREFIX + appUserDto.getUsername(), appUserDto);
	}

	@Async
	@Transactional
	public void sendNewMailVerifiyLinkAsync(String username, String email) {
		sendNewMailVerifiyLink(username, email);
	}

	private void sendNewMailVerifiyLink(String username, String email) {
		String token = UUID.randomUUID().toString();
		storeVerifyToken(username, token, atozMartConfig.emailVerifyTokenExpiry());
		URI verifyLink = authServerUtil.getVerifyLink(atozMartConfig.baseUrl(), authServerUtil.getEncodedToken(token));
		log.info("verify link: {}", verifyLink);
		mailVerifyLink(username, email, verifyLink.toString());
	}

	private void sendNewPwdResetLink(ForgotPasswordRequest request) {
		String token = UUID.randomUUID().toString();
		storeResetToken(request.username(), token, atozMartConfig.pwdResetTokenExpiry());
		URI resetLink = authServerUtil.getResetLink(atozMartConfig.baseUrl(), authServerUtil.getEncodedToken(token));
		log.info("reset link: {}", resetLink);
		mailResetLink(request.username(), resetLink.toString());
	}

	private void storeVerifyToken(String username, String token, Long expiry) {
		EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
		emailVerificationToken.setToken(token);
		AppUser appUser = new AppUser();
		appUser.setUsername(username);
		emailVerificationToken.setAppUser(appUser);
		emailVerificationToken.setExpiresAt(LocalDateTime.now().plusSeconds(expiry));
		notificationDao.saveEmailVerificationToken(emailVerificationToken);
	}

	private void storeResetToken(String username, String token, Long expiry) {
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setToken(token);
		AppUser appUser = new AppUser();
		appUser.setUsername(username);
		passwordResetToken.setAppUser(appUser);
		passwordResetToken.setExpiresAt(LocalDateTime.now().plusSeconds(expiry));
		notificationDao.savePasswordResetToken(passwordResetToken);
	}

	private void mailResetLink(String username, String resetLink) {
		String email = getEmail(username);
		log.info("sending reset link to: {}", email);

		String emailBody = AuthServerConstants.getPasswordResetEmailContent(username, resetLink);
		try {
			sendEmail(email, emailBody);
		} catch (Exception ex) {
			log.debug("exception sending password reset mail, {}", ex.getMessage());
			throw new AuthServerException("couldn't mail reset link at the moment, please try again later",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void mailVerifyLink(String username, String email, String resetLink) {

		if (email == null) {
			email = getEmail(username);
		}

		log.info("sending email verification link to: {}", email);
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
		return appUserDao.getUser(username).getMail();
	}

}
