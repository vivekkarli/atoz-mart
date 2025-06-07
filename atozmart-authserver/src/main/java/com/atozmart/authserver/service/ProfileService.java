package com.atozmart.authserver.service;

import java.util.Collections;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dto.SignUpForm;
import com.atozmart.authserver.dto.profile.BasicDetails;
import com.atozmart.authserver.dto.profile.ProfileDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

	private final StreamBridge streamBridge;

	private final AppUserDao appUserDao;

	public void createProfile(SignUpForm signUpForm) {

		BasicDetails basicDetails = new BasicDetails();
		basicDetails.setUsername(signUpForm.username());
		basicDetails.setFirstName(signUpForm.firstName());
		basicDetails.setLastName(signUpForm.lastName());
		basicDetails.setMail(signUpForm.mail());
		basicDetails.setMobileNo(signUpForm.mobileNo());

		ProfileDetails profileDetails = new ProfileDetails();
		profileDetails.setBasicDetails(basicDetails);
		profileDetails.setAddressDetails(Collections.emptyList());

		streamBridge.send("registerNewUser-out-0", profileDetails);

	}

	public void updateBasicDetails(String username, BasicDetails basicDetails) {
		
		appUserDao.updateBasicDetails(username, basicDetails);

	}

}
