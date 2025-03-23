package com.atozmart.notification.configuration;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.atozmart.notification.dto.MailContentDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
public class NotificationConfig {

	private JavaMailSender javaMailSender;

	@Bean
	public Function<MailContentDto, String> emailVerification() {
		return mailContent -> {
			log.info("sending mail");
			try {
				MimeMessage mimeMessage = javaMailSender.createMimeMessage();
				MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
				mimeMessageHelper.setTo(InternetAddress.parse(mailContent.to()));
				mimeMessageHelper.setText(mailContent.content(), true);
				//mimeMessageHelper.addAttachment("", mailContent.attachment());

				javaMailSender.send(mimeMessage);

			} catch (MessagingException e) {
				log.error("sending mail failed: {}", e.getMessage());
				return "failed";
			} 

			return "mail sent successfully";
		};

	}

}
