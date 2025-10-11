package com.atozmart.authserver.service;

import java.util.Collections;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dto.SignUpForm;
import com.atozmart.authserver.dto.profile.BasicDetailsDto;
import com.atozmart.authserver.dto.profile.ProfileDetailsDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

	private final StreamBridge streamBridge;

	private final AppUserDao appUserDao;

	public void createProfile(SignUpForm signUpForm) {

		BasicDetailsDto basicDetailsDto = new BasicDetailsDto(signUpForm.username(), signUpForm.firstName(),
				signUpForm.lastName(), signUpForm.mail(), signUpForm.mobileNo());

		ProfileDetailsDto profileDetailsDto = new ProfileDetailsDto(basicDetailsDto, Collections.emptyList());

		try {
			streamBridge.send("registerNewUser-out-0", profileDetailsDto);
		} catch (Exception ex) {
			log.debug("couldn't register new user, {}", ex.getMessage());
		}

	}

	@Async
	public void createProfileAsync(SignUpForm signUpForm) {
		createProfile(signUpForm);
	}

	public void updateBasicDetails(String username, BasicDetailsDto basicDetailsDto) {
		appUserDao.updateBasicDetails(username, basicDetailsDto);
	}

}
