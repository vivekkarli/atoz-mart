package com.atozmart.authserver.service;

import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.AuthServerUtil;
import com.atozmart.authserver.dao.NotificationDao;
import com.atozmart.authserver.dto.MailContentDto;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.entity.EmailVerification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {
	
	private NotificationDao notificationDao;
	
	private StreamBridge streamBridge;
	
	@Async
	public void sendEmailVerificationMail(AppUser appUser) {
		// step-1 generate random code
		String code = UUID.randomUUID().toString();
		String body = AuthServerUtil.getVerifyEmailContent(appUser.getUsername(), code);
		
		// step-2 call notification service
		MailContentDto mailContentDto = new MailContentDto(appUser.getMail(), null, body, null);
		streamBridge.send("emailVerification-in-0", mailContentDto);
		
		// step-3 update table
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setCode(code);
		emailVerification.setUsername(appUser.getUsername());
		emailVerification.setEmail(appUser.getMail());
		notificationDao.updateEmailVerificationTable(emailVerification);
	}
	
	public ResponseEntity<String> verifyEmail(String code) {
		notificationDao.verifyEmail(code);
		return new ResponseEntity<>("email verified successfully",HttpStatus.ACCEPTED);
	}

}
