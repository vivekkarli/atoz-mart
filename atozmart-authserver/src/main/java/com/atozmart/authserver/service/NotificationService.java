package com.atozmart.authserver.service;

import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.AuthServerUtil;
import com.atozmart.authserver.dao.NotificationDao;
import com.atozmart.authserver.dto.MailContentDto;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	
	private final NotificationDao notificationDao;
	
	private final StreamBridge streamBridge;
	
	public void sendEmailConfirmationMail(AppUser appUser) {
		// step-1 generate random code
		String code = UUID.randomUUID().toString();
		String body = AuthServerUtil.getConfirmEmailContent(appUser.getUsername(), code);
		
		// step-2 update table
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setCode(code);
		emailVerification.setUsername(appUser.getUsername());
		emailVerification.setEmail(appUser.getMail());
		emailVerification.setNotificationSent(false);
		notificationDao.updateEmailVerificationTable(emailVerification);
		
		// step-3 call notification service
		MailContentDto mailContentDto = new MailContentDto(appUser.getMail(), null, body, null);
		streamBridge.send("sendEmail-out-0", mailContentDto);
		
	}
	
	public ResponseEntity<String> confirmEmail(String code) {
		notificationDao.confirmEmail(code);
		return new ResponseEntity<>("email verified successfully",HttpStatus.ACCEPTED);
	}

}
