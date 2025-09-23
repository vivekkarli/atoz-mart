package com.atozmart.authserver.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.AuthServerUtil;
import com.atozmart.authserver.dao.NotificationDao;
import com.atozmart.authserver.dto.ForgotPasswordRequest;
import com.atozmart.authserver.dto.ForgotPasswordResponse;
import com.atozmart.authserver.dto.MailContentDto;
import com.atozmart.authserver.dto.ResetPasswordResponse;
import com.atozmart.authserver.dto.ResetPasswordRequest;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerification;
import com.atozmart.authserver.entity.PasswordResetToken;
import com.atozmart.authserver.exception.AuthServerException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationDao notificationDao;

	private final StreamBridge streamBridge;

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

	public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
		notificationDao.resetPassword(request.token(), request.newPassword());
		return new ResetPasswordResponse("password reset successfull");
	}

	public ForgotPasswordResponse sendPasswordResetMail(ForgotPasswordRequest request) {

		// check if there is already an un-expired token
		try {
			PasswordResetToken passwordResetToken = notificationDao
					.findPasswordResetTokenByUsername(request.username());
			if (!passwordResetToken.isExpired()) {
				// send email
				String email = getEmail(request.username());
				String emailBody = AuthServerUtil.getPasswordResetEmailContent(request.username(),
						passwordResetToken.getToken());
				sendEmail(email, emailBody);
				return new ForgotPasswordResponse("password reset link sent to your mail");
			}

		} catch (AuthServerException ex) {
			log.debug("exception finding existing token", ex.getMessage());
		}

		// generate random token, and set expiry date
		String token = UUID.randomUUID().toString();

		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setToken(token);

		AppUser appUser = new AppUser();
		appUser.setUsername(request.username());
		passwordResetToken.setAppUser(appUser);

		passwordResetToken.setExpiresAt(LocalDateTime.now().plusMinutes(60));

		// persist token into db
		notificationDao.updatePasswordResetTokenTable(passwordResetToken);

		// send email
		String email = getEmail(request.username());
		String emailBody = AuthServerUtil.getPasswordResetEmailContent(request.username(), token);
		sendEmail(email, emailBody);

		return new ForgotPasswordResponse("password reset link sent to your mail");

	}

	private void sendEmail(String email, String body) {
		MailContentDto mailContentDto = new MailContentDto(email, null, body, null);
		streamBridge.send("sendEmail-out-0", mailContentDto);
	}

	private String getEmail(String username) throws UsernameNotFoundException {
		return notificationDao.loadUserByUsername(username).getMail();
	}

}
